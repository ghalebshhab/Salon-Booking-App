package com.Backend.SalonBooking.Dtos.Bookings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerBookingActionRequest {

    private String ownerNote;

    private LocalTime ownerSuggestedTime;

    // Optional: owner can assign employee while accepting booking
    private Long assignedEmployeeId;
}