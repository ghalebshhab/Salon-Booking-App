package com.Backend.SalonBooking.Repositories;

import com.Backend.SalonBooking.Entities.Bookings.Booking;
import com.Backend.SalonBooking.Entities.Bookings.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingRepo extends JpaRepository<Booking, Long> {

    List<Booking> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<Booking> findBySalonIdOrderByCreatedAtDesc(Long salonId);

    List<Booking> findBySalonIdAndStatusOrderByCreatedAtDesc(Long salonId, BookingStatus status);

    List<Booking> findByAssignedEmployeeIdOrderByCreatedAtDesc(Long assignedEmployeeId);

    boolean existsBySalonIdAndBookingDateAndBookingTimeAndStatusIn(
            Long salonId,
            LocalDate bookingDate,
            LocalTime bookingTime,
            List<BookingStatus> statuses
    );

    boolean existsByAssignedEmployeeIdAndBookingDateAndBookingTimeAndStatusIn(
            Long assignedEmployeeId,
            LocalDate bookingDate,
            LocalTime bookingTime,
            List<BookingStatus> statuses
    );
}