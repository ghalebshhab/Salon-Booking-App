package com.Backend.SalonBooking.Repositories;

import com.Backend.SalonBooking.Entities.SalonEmployees.EmployeeStatus;
import com.Backend.SalonBooking.Entities.SalonEmployees.Salonemps;
import com.Backend.SalonBooking.Entities.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalonEmployeesRepo extends JpaRepository<Salonemps,Long> {
   List<User> existsSalonEmployeesBySalonId(Long salonId);
}
