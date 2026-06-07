package com.Backend.SalonBooking.Services.Review;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.Reviews.CreateReviewRequest;
import com.Backend.SalonBooking.Dtos.Reviews.ReviewResponse;
import com.Backend.SalonBooking.Dtos.Reviews.ReviewSummaryResponse;
import com.Backend.SalonBooking.Entities.Bookings.Booking;
import com.Backend.SalonBooking.Entities.Bookings.BookingStatus;
import com.Backend.SalonBooking.Entities.Reviews.Review;
import com.Backend.SalonBooking.Entities.Salons.Salon;
import com.Backend.SalonBooking.Entities.Users.User;
import com.Backend.SalonBooking.Repositories.BookingRepo;
import com.Backend.SalonBooking.Repositories.ReviewRepo;
import com.Backend.SalonBooking.Repositories.SalonRepo;
import com.Backend.SalonBooking.Repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepo reviewRepo;
    private final BookingRepo bookingRepo;
    private final SalonRepo salonRepo;
    private final UserRepo userRepo;

    @Override
    @Transactional
    public ApiResponse<ReviewResponse> createReview(CreateReviewRequest request) {

        User customer = getCurrentUser();

        if (request.getBookingId() == null) {
            return ApiResponse.error("Booking id is required");
        }

        if (request.getRating() < 1 || request.getRating() > 5) {
            return ApiResponse.error("Rating must be between 1 and 5");
        }

        Booking booking = bookingRepo.findById(request.getBookingId()).orElse(null);

        if (booking == null) {
            return ApiResponse.error("Booking not found");
        }

        if (booking.getCustomer() == null || !booking.getCustomer().getId().equals(customer.getId())) {
            return ApiResponse.error("You can only review your own booking");
        }

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            return ApiResponse.error("You can only review completed bookings");
        }

        if (reviewRepo.existsByBookingIdAndIsDeletedFalse(booking.getId())) {
            return ApiResponse.error("You already reviewed this booking");
        }

        if (booking.getSalon() == null) {
            return ApiResponse.error("Booking salon not found");
        }

        Review review = new Review();

        review.setCustomer(customer);
        review.setSalon(booking.getSalon());
        review.setBooking(booking);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setIsDeleted(false);

        Review saved = reviewRepo.save(review);

        return ApiResponse.success("Review created successfully", mapToResponse(saved));
    }

    @Override
    public ApiResponse<List<ReviewResponse>> getSalonReviews(Long salonId) {

        Salon salon = salonRepo.findById(salonId).orElse(null);

        if (salon == null) {
            return ApiResponse.error("Salon not found");
        }

        if (salon.getIsDeleted()) {
            return ApiResponse.error("Salon is deleted");
        }

        List<ReviewResponse> response = reviewRepo
                .findBySalonIdAndIsDeletedFalseOrderByCreatedAtDesc(salonId)
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ApiResponse.success("Salon reviews returned successfully", response);
    }

    @Override
    public ApiResponse<ReviewSummaryResponse> getSalonReviewSummary(Long salonId) {

        Salon salon = salonRepo.findById(salonId).orElse(null);

        if (salon == null) {
            return ApiResponse.error("Salon not found");
        }

        List<Review> reviews = reviewRepo.findBySalonIdAndIsDeletedFalseOrderByCreatedAtDesc(salonId);

        long totalReviews = reviews.size();

        double averageRating = 0;

        if (totalReviews > 0) {
            averageRating = reviews
                    .stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0);
        }

        averageRating = Math.round(averageRating * 10.0) / 10.0;

        ReviewSummaryResponse response = new ReviewSummaryResponse(
                salonId,
                averageRating,
                totalReviews
        );

        return ApiResponse.success("Salon review summary returned successfully", response);
    }

    @Override
    public ApiResponse<List<ReviewResponse>> getMyReviews() {

        User customer = getCurrentUser();

        List<ReviewResponse> response = reviewRepo
                .findByCustomerIdAndIsDeletedFalseOrderByCreatedAtDesc(customer.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ApiResponse.success("My reviews returned successfully", response);
    }

    @Override
    @Transactional
    public ApiResponse<String> deleteMyReview(Long reviewId) {

        User customer = getCurrentUser();

        Review review = reviewRepo.findById(reviewId).orElse(null);

        if (review == null || review.getIsDeleted()) {
            return ApiResponse.error("Review not found");
        }

        if (review.getCustomer() == null || !review.getCustomer().getId().equals(customer.getId())) {
            return ApiResponse.error("You can only delete your own review");
        }

        review.setIsDeleted(true);
        reviewRepo.save(review);

        return ApiResponse.success("Review deleted successfully", null);
    }

    private User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    private ReviewResponse mapToResponse(Review review) {

        ReviewResponse response = new ReviewResponse();

        response.setId(review.getId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());

        if (review.getBooking() != null) {
            response.setBookingId(review.getBooking().getId());
        }

        if (review.getSalon() != null) {
            response.setSalonId(review.getSalon().getId());
            response.setSalonName(review.getSalon().getName());
        }

        if (review.getCustomer() != null) {
            response.setCustomerId(review.getCustomer().getId());
            response.setCustomerEmail(review.getCustomer().getEmail());
            response.setCustomerName(getCustomerDisplayName(review.getCustomer()));
        }

        return response;
    }

    private String getCustomerDisplayName(User user) {

        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String username = user.getUsername();
        String email = user.getEmail();

        String name = "";

        if (firstName != null && !firstName.isBlank()) {
            name += firstName.trim();
        }

        if (lastName != null && !lastName.isBlank()) {
            name += " " + lastName.trim();
        }

        if (name.isBlank()) {
            if (username != null && !username.isBlank()) {
                name = username;
            } else if (email != null && !email.isBlank()) {
                name = email;
            } else {
                name = "Customer";
            }
        }

        return name.trim();
    }
}