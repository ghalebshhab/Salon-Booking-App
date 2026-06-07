package com.Backend.SalonBooking.Dtos.Bookings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {

    private Long salonId;

    // User can choose many services by checkboxes
    private List<Long> serviceIds;

    private String customerPhoneNumber;

    // You can send it as text from frontend, for example:
    // "lat: 31.95, lng: 35.91" or "Amman - Jordan"
    private String customerLocation;

    private LocalDate bookingDate;

    private LocalTime bookingTime;

    private String note;
}