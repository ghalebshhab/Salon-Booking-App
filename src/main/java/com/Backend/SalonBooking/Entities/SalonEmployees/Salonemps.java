package com.Backend.SalonBooking.Entities.SalonEmployees;

import com.Backend.SalonBooking.Entities.Salons.Salon;
import com.Backend.SalonBooking.Entities.Users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "salon_employees",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"salon_id", "employee_id"})
        })
        @Data
@AllArgsConstructor
@NoArgsConstructor
public class Salonemps {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false,name = "salon_id")
    @ManyToOne(cascade = CascadeType.ALL)
    private Salon salon;

    @JoinColumn(nullable = false,name = "employee_id")
    @ManyToOne(cascade = CascadeType.ALL)
    private User employee;

    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;

    private LocalDateTime joinedAt;

    private LocalDateTime leftAt;
}
