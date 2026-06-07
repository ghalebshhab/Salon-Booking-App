package com.Backend.SalonBooking.Dtos.Reviews;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReviewRequest {

    private Long bookingId;

    private int rating;

    private String comment;
}