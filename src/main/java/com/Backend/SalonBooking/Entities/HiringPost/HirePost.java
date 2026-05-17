package com.Backend.SalonBooking.Entities.HiringPost;

import com.Backend.SalonBooking.Entities.Salons.Salon;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HirePost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    private Integer neededEmployees;

    @Enumerated(EnumType.STRING)
    private HiringStatus status;

    private LocalDateTime createdAt;

    private String city;

    @ManyToOne
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;
}
