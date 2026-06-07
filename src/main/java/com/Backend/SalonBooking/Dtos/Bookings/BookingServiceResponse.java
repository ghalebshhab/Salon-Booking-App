package com.Backend.SalonBooking.Dtos.Bookings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingServiceResponse {

    private Long id;

    private String name;

    private double price;

    private int durationMinutes;
}