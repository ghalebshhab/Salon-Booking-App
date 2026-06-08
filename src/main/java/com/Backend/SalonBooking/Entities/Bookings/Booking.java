package com.Backend.SalonBooking.Entities.Bookings;

import com.Backend.SalonBooking.Entities.SalonEmployees.Salonemps;
import com.Backend.SalonBooking.Entities.SalonServices.SalonServicesItem;
import com.Backend.SalonBooking.Entities.Salons.Salon;
import com.Backend.SalonBooking.Entities.Users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The customer who made the booking
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    // The salon that receives the booking
    @ManyToOne
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    @ManyToOne
    @JoinColumn(name = "assigned_employee_id")
    private Salonemps assignedEmployee;

    // The selected services, because user can choose more than one service
    @ManyToMany
    @JoinTable(
            name = "booking_services",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<SalonServicesItem> services;

    @Column(nullable = false)
    private String customerPhoneNumber;

    @Column(length = 1000)
    private String customerLocation;

    @Column(nullable = false)
    private LocalDate bookingDate;

    @Column(nullable = false)
    private LocalTime bookingTime;

    // Owner can suggest another time if busy
    private LocalTime ownerSuggestedTime;

    @Column(length = 1000)
    private String note;

    @Column(length = 1000)
    private String ownerNote;

    @Column(nullable = false)
    private double totalPrice;

    @Column(nullable = false)
    private int totalDurationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (status == null) {
            status = BookingStatus.PENDING;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}