package com.Backend.SalonBooking.Services.Review;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.Reviews.CreateReviewRequest;
import com.Backend.SalonBooking.Dtos.Reviews.ReviewResponse;
import com.Backend.SalonBooking.Dtos.Reviews.ReviewSummaryResponse;

import java.util.List;

public interface ReviewService {

    ApiResponse<ReviewResponse> createReview(CreateReviewRequest request);

    ApiResponse<List<ReviewResponse>> getSalonReviews(Long salonId);

    ApiResponse<ReviewSummaryResponse> getSalonReviewSummary(Long salonId);

    ApiResponse<List<ReviewResponse>> getMyReviews();

    ApiResponse<String> deleteMyReview(Long reviewId);
}