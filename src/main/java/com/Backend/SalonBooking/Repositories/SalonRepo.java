package com.Backend.SalonBooking.Repositories;

import com.Backend.SalonBooking.Entities.Salons.Salon;
import com.Backend.SalonBooking.Entities.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalonRepo extends JpaRepository<Salon, Long> {
    Optional<Salon> findByowner(User owner);
    boolean existsByphoneNumber(String phoneNumber);
    Optional<Salon> findByemail(String email);
    Optional<Salon> findById(Long id);
    int currentNumOfEmployeesById(Long id);
    int maxNumOfEmployeesById(Long id);
}
