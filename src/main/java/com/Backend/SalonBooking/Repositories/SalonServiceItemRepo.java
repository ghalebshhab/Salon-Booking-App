package com.Backend.SalonBooking.Repositories;
import com.Backend.SalonBooking.Entities.SalonServices.SalonServicesItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalonServiceItemRepo extends JpaRepository<SalonServicesItem, Long> {

    List<SalonServicesItem> findBySalonIdAndIsActiveTrue(Long salonId);

    List<SalonServicesItem> findBySalonId(Long salonId);
}