package com.Backend.SalonBooking.Controllers.Review;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.Reviews.CreateReviewRequest;
import com.Backend.SalonBooking.Dtos.Reviews.ReviewResponse;
import com.Backend.SalonBooking.Dtos.Reviews.ReviewSummaryResponse;
import com.Backend.SalonBooking.Services.Review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"})
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @RequestBody CreateReviewRequest request
    ) {
        return ResponseEntity.ok(reviewService.createReview(request));
    }

    @GetMapping("/salon/{salonId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getSalonReviews(
            @PathVariable Long salonId
    ) {
        return ResponseEntity.ok(reviewService.getSalonReviews(salonId));
    }

    @GetMapping("/salon/{salonId}/summary")
    public ResponseEntity<ApiResponse<ReviewSummaryResponse>> getSalonReviewSummary(
            @PathVariable Long salonId
    ) {
        return ResponseEntity.ok(reviewService.getSalonReviewSummary(salonId));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getMyReviews() {
        return ResponseEntity.ok(reviewService.getMyReviews());
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<String>> deleteMyReview(
            @PathVariable Long reviewId
    ) {
        return ResponseEntity.ok(reviewService.deleteMyReview(reviewId));
    }
}