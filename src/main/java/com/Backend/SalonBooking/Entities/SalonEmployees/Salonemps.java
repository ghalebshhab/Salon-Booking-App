package com.Backend.SalonBooking.Entities.SalonEmployees;

import com.Backend.SalonBooking.Entities.Salons.Salon;
import com.Backend.SalonBooking.Entities.Users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "salon_employees")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Salonemps {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false, name = "salon_id")
    @ManyToOne
    private Salon salon;

    // Nullable because employee may not have account yet
    @JoinColumn(name = "employee_id")
    @ManyToOne
    private User employee;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    private String imageUrl;

    private String specialty;

    private LocalTime startTime;

    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeStatus status = EmployeeStatus.INVITED;

    private Boolean invitationSent = false;

    private LocalDateTime invitationSentAt;

    private LocalDateTime joinedAt;

    private LocalDateTime leftAt;

    @PrePersist
    public void onCreate() {
        if (status == null) {
            status = EmployeeStatus.INVITED;
        }

        if (invitationSent == null) {
            invitationSent = false;
        }
    }
}
