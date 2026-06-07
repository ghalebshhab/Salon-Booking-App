package com.Backend.SalonBooking.Entities.Reviews;

import com.Backend.SalonBooking.Entities.Bookings.Booking;
import com.Backend.SalonBooking.Entities.Salons.Salon;
import com.Backend.SalonBooking.Entities.Users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Customer who wrote the review
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    // Salon being reviewed
    @ManyToOne
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    // The completed booking that allows this review
    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @Column(nullable = false)
    private int rating;

    @Column(length = 1000)
    private String comment;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (isDeleted == null) {
            isDeleted = false;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}