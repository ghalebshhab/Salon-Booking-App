package com.Backend.SalonBooking.Repositories;

import com.Backend.SalonBooking.Entities.SalonProfileMedia.MediaType;
import com.Backend.SalonBooking.Entities.SalonProfileMedia.SalonMediaPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalonMediaRepo extends JpaRepository<SalonMediaPost, Long> {

    List<SalonMediaPost> findBySalonIdOrderByCreatedAtDesc(Long salonId);

    List<SalonMediaPost> findBySalonIdAndMediaTypeOrderByCreatedAtDesc(
            Long salonId,
            MediaType mediaType
    );
}