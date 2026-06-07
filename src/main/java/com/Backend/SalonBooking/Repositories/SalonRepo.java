package com.Backend.SalonBooking.Repositories;

import com.Backend.SalonBooking.Entities.Salons.Salon;
import com.Backend.SalonBooking.Entities.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalonRepo extends JpaRepository<Salon, Long> {

    Optional<Salon> findByOwner(User owner);

    Optional<Salon> findByOwnerId(Long ownerId);

    boolean existsByOwnerId(Long ownerId);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<Salon> findByEmail(String email);
    List<Salon> findByIsDeletedFalse();
}
