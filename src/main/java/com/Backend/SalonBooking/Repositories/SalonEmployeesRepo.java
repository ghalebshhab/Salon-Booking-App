package com.Backend.SalonBooking.Repositories;

import com.Backend.SalonBooking.Entities.SalonEmployees.EmployeeStatus;
import com.Backend.SalonBooking.Entities.SalonEmployees.Salonemps;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalonEmployeesRepo extends JpaRepository<Salonemps, Long> {

   boolean existsByEmployeeIdAndStatus(Long employeeId, EmployeeStatus status);

   long countBySalonIdAndStatus(Long salonId, EmployeeStatus status);

   Optional<Salonemps> findByEmployeeIdAndStatus(Long employeeId, EmployeeStatus status);

   Optional<Salonemps> findBySalonIdAndEmployeeIdAndStatus(
           Long salonId,
           Long employeeId,
           EmployeeStatus status
   );

   List<Salonemps> findBySalonIdAndStatus(Long salonId, EmployeeStatus status);

   List<Salonemps> findBySalonIdAndStatusIn(Long salonId, List<EmployeeStatus> statuses);

   Optional<Salonemps> findFirstByEmailIgnoreCaseAndStatus(String email, EmployeeStatus status);

   Optional<Salonemps> findFirstByPhoneNumberAndStatus(String phoneNumber, EmployeeStatus status);

   boolean existsBySalonIdAndEmailIgnoreCaseAndStatusIn(
           Long salonId,
           String email,
           List<EmployeeStatus> statuses
   );

   boolean existsBySalonIdAndPhoneNumberAndStatusIn(
           Long salonId,
           String phoneNumber,
           List<EmployeeStatus> statuses
   );
}
