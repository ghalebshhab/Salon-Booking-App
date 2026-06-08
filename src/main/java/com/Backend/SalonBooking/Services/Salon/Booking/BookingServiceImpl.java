package com.Backend.SalonBooking.Services.Salon.Booking;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.Bookings.BookingResponse;
import com.Backend.SalonBooking.Dtos.Bookings.BookingServiceResponse;
import com.Backend.SalonBooking.Dtos.Bookings.CreateBookingRequest;
import com.Backend.SalonBooking.Dtos.Bookings.OwnerBookingActionRequest;
import com.Backend.SalonBooking.Entities.Bookings.Booking;
import com.Backend.SalonBooking.Entities.Bookings.BookingStatus;
import com.Backend.SalonBooking.Entities.SalonEmployees.EmployeeStatus;
import com.Backend.SalonBooking.Entities.SalonEmployees.Salonemps;
import com.Backend.SalonBooking.Entities.SalonServices.SalonServicesItem;
import com.Backend.SalonBooking.Entities.Salons.Salon;
import com.Backend.SalonBooking.Entities.Users.User;
import com.Backend.SalonBooking.Repositories.BookingRepo;
import com.Backend.SalonBooking.Repositories.SalonEmployeesRepo;
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
    private final SalonEmployeesRepo salonEmployeesRepo;

    @Override
    @Transactional
    public ApiResponse<BookingResponse> createBooking(CreateBookingRequest request) {

        User customer = getCurrentUserOrNull();

        if (customer == null) {
            return ApiResponse.error("Authenticated user not found");
        }

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

        boolean salonTimeAlreadyBooked = bookingRepo.existsBySalonIdAndBookingDateAndBookingTimeAndStatusIn(
                salon.getId(),
                request.getBookingDate(),
                request.getBookingTime(),
                List.of(BookingStatus.PENDING, BookingStatus.ACCEPTED)
        );

        if (salonTimeAlreadyBooked) {
            return ApiResponse.error("This time is already booked or pending");
        }

        Salonemps assignedEmployee = null;

        if (request.getAssignedEmployeeId() != null) {
            assignedEmployee = validateAssignedEmployee(
                    request.getAssignedEmployeeId(),
                    salon,
                    request.getBookingDate(),
                    request.getBookingTime()
            );

            if (assignedEmployee == null) {
                return ApiResponse.error("Selected employee is not valid or not available");
            }
        }

        List<SalonServicesItem> selectedServices = new ArrayList<>();

        double totalPrice = 0;
        int totalDuration = 0;

        for (Long serviceId : request.getServiceIds()) {

            SalonServicesItem serviceItem = salonServiceItemRepo.findById(serviceId).orElse(null);

            if (serviceItem == null) {
                return ApiResponse.error("Service not found with id: " + serviceId);
            }

            if (serviceItem.getSalon() == null || !serviceItem.getSalon().getId().equals(salon.getId())) {
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
        booking.setAssignedEmployee(assignedEmployee);
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

        User customer = getCurrentUserOrNull();

        if (customer == null) {
            return ApiResponse.error("Authenticated user not found");
        }

        List<BookingResponse> response = bookingRepo
                .findByCustomerIdOrderByCreatedAtDesc(customer.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ApiResponse.success("My bookings returned successfully", response);
    }

    @Override
    public ApiResponse<List<BookingResponse>> getMySalonBookings() {

        User owner = getCurrentUserOrNull();

        if (owner == null) {
            return ApiResponse.error("Authenticated user not found");
        }

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

        User owner = getCurrentUserOrNull();

        if (owner == null) {
            return ApiResponse.error("Authenticated user not found");
        }

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
    public ApiResponse<List<BookingResponse>> getMyEmployeeBookings() {

        User employeeUser = getCurrentUserOrNull();

        if (employeeUser == null) {
            return ApiResponse.error("Authenticated user not found");
        }

        Salonemps employee = salonEmployeesRepo
                .findByEmployeeIdAndStatus(employeeUser.getId(), EmployeeStatus.ACTIVE)
                .orElse(null);

        if (employee == null) {
            return ApiResponse.error("You are not linked as an active employee");
        }

        List<BookingResponse> response = bookingRepo
                .findByAssignedEmployeeIdOrderByCreatedAtDesc(employee.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ApiResponse.success("Employee bookings returned successfully", response);
    }

    @Override
    @Transactional
    public ApiResponse<BookingResponse> acceptBooking(Long bookingId, OwnerBookingActionRequest request) {

        User owner = getCurrentUserOrNull();

        if (owner == null) {
            return ApiResponse.error("Authenticated user not found");
        }

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

        if (request != null && request.getAssignedEmployeeId() != null) {
            Salonemps assignedEmployee = validateAssignedEmployee(
                    request.getAssignedEmployeeId(),
                    booking.getSalon(),
                    booking.getBookingDate(),
                    booking.getBookingTime()
            );

            if (assignedEmployee == null) {
                return ApiResponse.error("Selected employee is not valid or not available");
            }

            booking.setAssignedEmployee(assignedEmployee);
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

        User owner = getCurrentUserOrNull();

        if (owner == null) {
            return ApiResponse.error("Authenticated user not found");
        }

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

        User customer = getCurrentUserOrNull();

        if (customer == null) {
            return ApiResponse.error("Authenticated user not found");
        }

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

        User owner = getCurrentUserOrNull();

        if (owner == null) {
            return ApiResponse.error("Authenticated user not found");
        }

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

    @Override
    @Transactional
    public ApiResponse<BookingResponse> assignEmployee(Long bookingId, Long employeeId) {

        User owner = getCurrentUserOrNull();

        if (owner == null) {
            return ApiResponse.error("Authenticated user not found");
        }

        if (employeeId == null) {
            return ApiResponse.error("Employee id is required");
        }

        Booking booking = bookingRepo.findById(bookingId).orElse(null);

        if (booking == null) {
            return ApiResponse.error("Booking not found");
        }

        if (!isSalonOwner(owner, booking.getSalon())) {
            return ApiResponse.error("You can only assign employees for your own salon");
        }

        if (booking.getStatus() == BookingStatus.REJECTED ||
                booking.getStatus() == BookingStatus.CANCELLED ||
                booking.getStatus() == BookingStatus.COMPLETED) {
            return ApiResponse.error("You cannot assign employee to this booking status");
        }

        Salonemps employee = validateAssignedEmployee(
                employeeId,
                booking.getSalon(),
                booking.getBookingDate(),
                booking.getBookingTime()
        );

        if (employee == null) {
            return ApiResponse.error("Selected employee is not valid or not available");
        }

        booking.setAssignedEmployee(employee);

        Booking saved = bookingRepo.save(booking);

        return ApiResponse.success("Employee assigned successfully", mapToResponse(saved));
    }

    private Salonemps validateAssignedEmployee(
            Long employeeId,
            Salon salon,
            LocalDate bookingDate,
            java.time.LocalTime bookingTime
    ) {

        if (employeeId == null || salon == null || bookingDate == null || bookingTime == null) {
            return null;
        }

        Salonemps employee = salonEmployeesRepo.findById(employeeId).orElse(null);

        if (employee == null) {
            return null;
        }

        if (employee.getSalon() == null || !employee.getSalon().getId().equals(salon.getId())) {
            return null;
        }

        if (employee.getStatus() != EmployeeStatus.ACTIVE) {
            return null;
        }

        if (employee.getStartTime() != null && bookingTime.isBefore(employee.getStartTime())) {
            return null;
        }

        if (employee.getEndTime() != null && bookingTime.isAfter(employee.getEndTime())) {
            return null;
        }

        boolean employeeBusy = bookingRepo.existsByAssignedEmployeeIdAndBookingDateAndBookingTimeAndStatusIn(
                employee.getId(),
                bookingDate,
                bookingTime,
                List.of(BookingStatus.PENDING, BookingStatus.ACCEPTED)
        );

        return employeeBusy ? null : employee;
    }

    private User getCurrentUserOrNull() {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepo.findByEmail(email).orElse(null);
    }

    private boolean isSalonOwner(User owner, Salon salon) {

        if (owner == null || salon == null || salon.getOwner() == null) {
            return false;
        }

        return salon.getOwner().getId().equals(owner.getId());
    }

    private BookingResponse mapToResponse(Booking booking) {

        BookingResponse response = new BookingResponse();

        response.setId(booking.getId());

        if (booking.getCustomer() != null) {
            response.setCustomerId(booking.getCustomer().getId());
            response.setCustomerName(getUserDisplayName(booking.getCustomer()));
            response.setCustomerEmail(booking.getCustomer().getEmail());
        }

        response.setCustomerPhoneNumber(booking.getCustomerPhoneNumber());
        response.setCustomerLocation(booking.getCustomerLocation());

        if (booking.getSalon() != null) {
            response.setSalonId(booking.getSalon().getId());
            response.setSalonName(booking.getSalon().getName());
        }

        if (booking.getAssignedEmployee() != null) {
            Salonemps employee = booking.getAssignedEmployee();

            response.setAssignedEmployeeId(employee.getId());
            response.setAssignedEmployeeName(employee.getFullName());
            response.setAssignedEmployeeEmail(employee.getEmail());
            response.setAssignedEmployeePhoneNumber(employee.getPhoneNumber());
            response.setAssignedEmployeeSpecialty(employee.getSpecialty());
            response.setAssignedEmployeeImageUrl(employee.getImageUrl());

            if (employee.getEmployee() != null) {
                response.setAssignedEmployeeUserId(employee.getEmployee().getId());
            }
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

    private String getUserDisplayName(User user) {

        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String username = user.getUsername();
        String email = user.getEmail();

        String name = "";

        if (firstName != null && !firstName.isBlank()) {
            name += firstName.trim();
        }

        if (lastName != null && !lastName.isBlank()) {
            name += " " + lastName.trim();
        }

        if (name.isBlank()) {
            if (username != null && !username.isBlank()) {
                name = username;
            } else if (email != null && !email.isBlank()) {
                name = email;
            } else {
                name = "User";
            }
        }

        return name.trim();
    }
}