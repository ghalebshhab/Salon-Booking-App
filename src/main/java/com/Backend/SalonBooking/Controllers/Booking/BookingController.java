package com.Backend.SalonBooking.Controllers.Booking;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.Bookings.BookingResponse;
import com.Backend.SalonBooking.Dtos.Bookings.CreateBookingRequest;
import com.Backend.SalonBooking.Dtos.Bookings.OwnerBookingActionRequest;
import com.Backend.SalonBooking.Services.Salon.Booking.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"})
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @RequestBody CreateBookingRequest request
    ) {
        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings() {
        return ResponseEntity.ok(bookingService.getMyBookings());
    }

    @GetMapping("/my-salon")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMySalonBookings() {
        return ResponseEntity.ok(bookingService.getMySalonBookings());
    }

    @GetMapping("/my-salon/pending")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMySalonPendingBookings() {
        return ResponseEntity.ok(bookingService.getMySalonPendingBookings());
    }

    @PutMapping("/{bookingId}/accept")
    public ResponseEntity<ApiResponse<BookingResponse>> acceptBooking(
            @PathVariable Long bookingId,
            @RequestBody(required = false) OwnerBookingActionRequest request
    ) {
        return ResponseEntity.ok(bookingService.acceptBooking(bookingId, request));
    }

    @PutMapping("/{bookingId}/reject")
    public ResponseEntity<ApiResponse<BookingResponse>> rejectBooking(
            @PathVariable Long bookingId,
            @RequestBody(required = false) OwnerBookingActionRequest request
    ) {
        return ResponseEntity.ok(bookingService.rejectBooking(bookingId, request));
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelMyBooking(
            @PathVariable Long bookingId
    ) {
        return ResponseEntity.ok(bookingService.cancelMyBooking(bookingId));
    }

    @PutMapping("/{bookingId}/complete")
    public ResponseEntity<ApiResponse<BookingResponse>> completeBooking(
            @PathVariable Long bookingId
    ) {
        return ResponseEntity.ok(bookingService.completeBooking(bookingId));
    }
}