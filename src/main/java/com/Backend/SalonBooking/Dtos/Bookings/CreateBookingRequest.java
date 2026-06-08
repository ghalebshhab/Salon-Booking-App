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

    private List<Long> serviceIds;

    // Optional: customer can choose employee, or leave it null for any employee
    private Long assignedEmployeeId;

    private String customerPhoneNumber;

    private String customerLocation;

    private LocalDate bookingDate;

    private LocalTime bookingTime;

    private String note;
}