package com.Backend.SalonBooking.Dtos.Reviews;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {

    private Long id;

    private Long bookingId;

    private Long salonId;

    private String salonName;

    private Long customerId;

    private String customerName;

    private String customerEmail;

    private int rating;

    private String comment;

    private LocalDateTime createdAt;
}