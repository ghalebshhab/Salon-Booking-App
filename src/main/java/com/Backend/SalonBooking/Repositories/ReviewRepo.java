package com.Backend.SalonBooking.Repositories;

import com.Backend.SalonBooking.Entities.Reviews.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepo extends JpaRepository<Review, Long> {

    List<Review> findBySalonIdAndIsDeletedFalseOrderByCreatedAtDesc(Long salonId);

    List<Review> findByCustomerIdAndIsDeletedFalseOrderByCreatedAtDesc(Long customerId);

    Optional<Review> findByBookingIdAndIsDeletedFalse(Long bookingId);

    boolean existsByBookingIdAndIsDeletedFalse(Long bookingId);

    long countBySalonIdAndIsDeletedFalse(Long salonId);
}