package com.Backend.SalonBooking.Services.Salon.Booking;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.Bookings.BookingResponse;
import com.Backend.SalonBooking.Dtos.Bookings.BookingServiceResponse;
import com.Backend.SalonBooking.Dtos.Bookings.CreateBookingRequest;
import com.Backend.SalonBooking.Dtos.Bookings.OwnerBookingActionRequest;
import com.Backend.SalonBooking.Entities.Bookings.Booking;
import com.Backend.SalonBooking.Entities.Bookings.BookingStatus;
import com.Backend.SalonBooking.Entities.SalonServices.SalonServicesItem;
import com.Backend.SalonBooking.Entities.Salons.Salon;
import com.Backend.SalonBooking.Entities.Users.User;
import com.Backend.SalonBooking.Repositories.BookingRepo;
import com.Backend.SalonBooking.Repositories.SalonRepo;
import com.Backend.SalonBooking.Repositories.SalonServiceItemRepo;
import com.Backend.SalonBooking.Repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepo bookingRepo;
    private final UserRepo userRepo;
    private final SalonRepo salonRepo;
    private final SalonServiceItemRepo salonServiceItemRepo;

    @Override
    @Transactional
    public ApiResponse<BookingResponse> createBooking(CreateBookingRequest request) {

        User customer = getCurrentUser();

        if (request.getSalonId() == null) {
            return ApiResponse.error("Salon id is required");
        }

        if (request.getServiceIds() == null || request.getServiceIds().isEmpty()) {
            return ApiResponse.error("You must choose at least one service");
        }

        if (request.getCustomerPhoneNumber() == null || request.getCustomerPhoneNumber().isBlank()) {
            return ApiResponse.error("Phone number is required");
        }

        if (request.getCustomerLocation() == null || request.getCustomerLocation().isBlank()) {
            return ApiResponse.error("Customer location is required");
        }

        if (request.getBookingDate() == null) {
            return ApiResponse.error("Booking date is required");
        }

        if (request.getBookingTime() == null) {
            return ApiResponse.error("Booking time is required");
        }

        if (request.getBookingDate().isBefore(LocalDate.now())) {
            return ApiResponse.error("Booking date cannot be in the past");
        }

        Salon salon = salonRepo.findById(request.getSalonId()).orElse(null);

        if (salon == null) {
            return ApiResponse.error("Salon not found");
        }

        if (salon.getIsDeleted()) {
            return ApiResponse.error("Salon is deleted");
        }

        if (salon.getOwner() != null && salon.getOwner().getId().equals(customer.getId())) {
            return ApiResponse.error("You cannot book your own salon");
        }

        if (request.getBookingTime().isBefore(salon.getOpenTime()) ||
                request.getBookingTime().isAfter(salon.getCloseTime())) {
            return ApiResponse.error("Booking time must be between salon open and close time");
        }

        boolean timeAlreadyBooked = bookingRepo.existsBySalonIdAndBookingDateAndBookingTimeAndStatusIn(
                salon.getId(),
                request.getBookingDate(),
                request.getBookingTime(),
                List.of(BookingStatus.PENDING, BookingStatus.ACCEPTED)
        );

        if (timeAlreadyBooked) {
            return ApiResponse.error("This time is already booked or pending");
        }

        List<SalonServicesItem> selectedServices = new ArrayList<>();

        double totalPrice = 0;
        int totalDuration = 0;

        for (Long serviceId : request.getServiceIds()) {

            SalonServicesItem serviceItem = salonServiceItemRepo.findById(serviceId).orElse(null);

            if (serviceItem == null) {
                return ApiResponse.error("Service not found with id: " + serviceId);
            }

            if (!serviceItem.getSalon().getId().equals(salon.getId())) {
                return ApiResponse.error("All selected services must belong to the same salon");
            }

            if (!serviceItem.getIsActive()) {
                return ApiResponse.error("Service is not active: " + serviceItem.getName());
            }

            selectedServices.add(serviceItem);
            totalPrice += serviceItem.getPrice();
            totalDuration += serviceItem.getDurationMinutes();
        }

        Booking booking = new Booking();

        booking.setCustomer(customer);
        booking.setSalon(salon);
        booking.setServices(selectedServices);
        booking.setCustomerPhoneNumber(request.getCustomerPhoneNumber().trim());
        booking.setCustomerLocation(request.getCustomerLocation().trim());
        booking.setBookingDate(request.getBookingDate());
        booking.setBookingTime(request.getBookingTime());
        booking.setNote(request.getNote());
        booking.setTotalPrice(totalPrice);
        booking.setTotalDurationMinutes(totalDuration);
        booking.setStatus(BookingStatus.PENDING);

        Booking saved = bookingRepo.save(booking);

        return ApiResponse.success("Booking created successfully and waiting for salon owner approval", mapToResponse(saved));
    }

    @Override
    public ApiResponse<List<BookingResponse>> getMyBookings() {

        User customer = getCurrentUser();

        List<BookingResponse> response = bookingRepo
                .findByCustomerIdOrderByCreatedAtDesc(customer.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ApiResponse.success("My bookings returned successfully", response);
    }

    @Override
    public ApiResponse<List<BookingResponse>> getMySalonBookings() {

        User owner = getCurrentUser();

        Salon salon = salonRepo.findByOwnerId(owner.getId()).orElse(null);

        if (salon == null) {
            return ApiResponse.error("You do not have a salon");
        }

        List<BookingResponse> response = bookingRepo
                .findBySalonIdOrderByCreatedAtDesc(salon.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ApiResponse.success("Salon bookings returned successfully", response);
    }

    @Override
    public ApiResponse<List<BookingResponse>> getMySalonPendingBookings() {

        User owner = getCurrentUser();

        Salon salon = salonRepo.findByOwnerId(owner.getId()).orElse(null);

        if (salon == null) {
            return ApiResponse.error("You do not have a salon");
        }

        List<BookingResponse> response = bookingRepo
                .findBySalonIdAndStatusOrderByCreatedAtDesc(salon.getId(), BookingStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ApiResponse.success("Pending salon bookings returned successfully", response);
    }

    @Override
    @Transactional
    public ApiResponse<BookingResponse> acceptBooking(Long bookingId, OwnerBookingActionRequest request) {

        User owner = getCurrentUser();

        Booking booking = bookingRepo.findById(bookingId).orElse(null);

        if (booking == null) {
            return ApiResponse.error("Booking not found");
        }

        if (!isSalonOwner(owner, booking.getSalon())) {
            return ApiResponse.error("You can only accept bookings for your own salon");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            return ApiResponse.error("Only pending bookings can be accepted");
        }

        booking.setStatus(BookingStatus.ACCEPTED);

        if (request != null) {
            booking.setOwnerNote(request.getOwnerNote());
            booking.setOwnerSuggestedTime(request.getOwnerSuggestedTime());
        }

        Booking saved = bookingRepo.save(booking);

        return ApiResponse.success("Booking accepted successfully", mapToResponse(saved));
    }

    @Override
    @Transactional
    public ApiResponse<BookingResponse> rejectBooking(Long bookingId, OwnerBookingActionRequest request) {

        User owner = getCurrentUser();

        Booking booking = bookingRepo.findById(bookingId).orElse(null);

        if (booking == null) {
            return ApiResponse.error("Booking not found");
        }

        if (!isSalonOwner(owner, booking.getSalon())) {
            return ApiResponse.error("You can only reject bookings for your own salon");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            return ApiResponse.error("Only pending bookings can be rejected");
        }

        booking.setStatus(BookingStatus.REJECTED);

        if (request != null) {
            booking.setOwnerNote(request.getOwnerNote());
            booking.setOwnerSuggestedTime(request.getOwnerSuggestedTime());
        }

        Booking saved = bookingRepo.save(booking);

        return ApiResponse.success("Booking rejected successfully", mapToResponse(saved));
    }

    @Override
    @Transactional
    public ApiResponse<BookingResponse> cancelMyBooking(Long bookingId) {

        User customer = getCurrentUser();

        Booking booking = bookingRepo.findById(bookingId).orElse(null);

        if (booking == null) {
            return ApiResponse.error("Booking not found");
        }

        if (!booking.getCustomer().getId().equals(customer.getId())) {
            return ApiResponse.error("You can only cancel your own booking");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            return ApiResponse.error("Completed booking cannot be cancelled");
        }

        if (booking.getStatus() == BookingStatus.REJECTED) {
            return ApiResponse.error("Rejected booking cannot be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        Booking saved = bookingRepo.save(booking);

        return ApiResponse.success("Booking cancelled successfully", mapToResponse(saved));
    }

    @Override
    @Transactional
    public ApiResponse<BookingResponse> completeBooking(Long bookingId) {

        User owner = getCurrentUser();

        Booking booking = bookingRepo.findById(bookingId).orElse(null);

        if (booking == null) {
            return ApiResponse.error("Booking not found");
        }

        if (!isSalonOwner(owner, booking.getSalon())) {
            return ApiResponse.error("You can only complete bookings for your own salon");
        }

        if (booking.getStatus() != BookingStatus.ACCEPTED) {
            return ApiResponse.error("Only accepted bookings can be completed");
        }

        booking.setStatus(BookingStatus.COMPLETED);

        Booking saved = bookingRepo.save(booking);

        return ApiResponse.success("Booking completed successfully", mapToResponse(saved));
    }

    private User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    private boolean isSalonOwner(User owner, Salon salon) {

        if (salon == null || salon.getOwner() == null) {
            return false;
        }

        return salon.getOwner().getId().equals(owner.getId());
    }

    private BookingResponse mapToResponse(Booking booking) {

        BookingResponse response = new BookingResponse();

        response.setId(booking.getId());

        if (booking.getCustomer() != null) {
            response.setCustomerId(booking.getCustomer().getId());
            String firstName = booking.getCustomer().getFirstName();
            String lastName = booking.getCustomer().getLastName();
            String username = booking.getCustomer().getUsername();
            String email = booking.getCustomer().getEmail();

            String customerName = "";

            if (firstName != null && !firstName.isBlank()) {
                customerName += firstName.trim();
            }

            if (lastName != null && !lastName.isBlank()) {
                customerName += " " + lastName.trim();
            }

            if (customerName.isBlank()) {
                if (username != null && !username.isBlank()) {
                    customerName = username;
                } else {
                    customerName = email;
                }
            }

            response.setCustomerName(customerName);
            response.setCustomerEmail(booking.getCustomer().getEmail());
        }

        response.setCustomerPhoneNumber(booking.getCustomerPhoneNumber());
        response.setCustomerLocation(booking.getCustomerLocation());

        if (booking.getSalon() != null) {
            response.setSalonId(booking.getSalon().getId());
            response.setSalonName(booking.getSalon().getName());
        }

        if (booking.getServices() != null) {
            response.setServices(
                    booking.getServices()
                            .stream()
                            .map(service -> new BookingServiceResponse(
                                    service.getId(),
                                    service.getName(),
                                    service.getPrice(),
                                    service.getDurationMinutes()
                            ))
                            .toList()
            );
        }

        response.setBookingDate(booking.getBookingDate());
        response.setBookingTime(booking.getBookingTime());
        response.setOwnerSuggestedTime(booking.getOwnerSuggestedTime());
        response.setNote(booking.getNote());
        response.setOwnerNote(booking.getOwnerNote());
        response.setTotalPrice(booking.getTotalPrice());
        response.setTotalDurationMinutes(booking.getTotalDurationMinutes());
        response.setStatus(booking.getStatus());

        return response;
    }
}