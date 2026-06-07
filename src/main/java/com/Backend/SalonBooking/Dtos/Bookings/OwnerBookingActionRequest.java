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

    // Optional: owner can suggest another time if he is busy
    private LocalTime ownerSuggestedTime;
}