package com.Backend.SalonBooking.Dtos.Bookings;

import com.Backend.SalonBooking.Entities.Bookings.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private Long id;

    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhoneNumber;
    private String customerLocation;

    private Long salonId;
    private String salonName;

    private List<BookingServiceResponse> services;

    private LocalDate bookingDate;
    private LocalTime bookingTime;
    private LocalTime ownerSuggestedTime;

    private String note;
    private String ownerNote;

    private double totalPrice;
    private int totalDurationMinutes;

    private BookingStatus status;
}