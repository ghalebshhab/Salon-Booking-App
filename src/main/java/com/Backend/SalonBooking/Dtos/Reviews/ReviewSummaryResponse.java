package com.Backend.SalonBooking.Dtos.Reviews;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewSummaryResponse {

    private Long salonId;

    private double averageRating;

    private long totalReviews;
}