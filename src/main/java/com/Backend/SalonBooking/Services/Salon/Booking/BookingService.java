package com.Backend.SalonBooking.Services.Salon.Booking;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.Bookings.BookingResponse;
import com.Backend.SalonBooking.Dtos.Bookings.CreateBookingRequest;
import com.Backend.SalonBooking.Dtos.Bookings.OwnerBookingActionRequest;

import java.util.List;

public interface BookingService {

    ApiResponse<BookingResponse> createBooking(CreateBookingRequest request);

    ApiResponse<List<BookingResponse>> getMyBookings();

    ApiResponse<List<BookingResponse>> getMySalonBookings();

    ApiResponse<List<BookingResponse>> getMySalonPendingBookings();

    ApiResponse<BookingResponse> acceptBooking(Long bookingId, OwnerBookingActionRequest request);

    ApiResponse<BookingResponse> rejectBooking(Long bookingId, OwnerBookingActionRequest request);

    ApiResponse<BookingResponse> cancelMyBooking(Long bookingId);

    ApiResponse<BookingResponse> completeBooking(Long bookingId);
}