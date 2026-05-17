package com.Backend.SalonBooking.Repositories;

import com.Backend.SalonBooking.Entities.HiringPost.HirePost;
import com.Backend.SalonBooking.Entities.HiringPost.HiringStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalonHiringPostRepo extends JpaRepository<HirePost,Long> {
    List<HirePost> findByStatus(HiringStatus status);

    Optional<HirePost> findBySalonIdAndStatus(Long salonId, HiringStatus status);
}
