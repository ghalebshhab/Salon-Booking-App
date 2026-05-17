package com.Backend.SalonBooking.Dtos.Salons;

import com.Backend.SalonBooking.Entities.SalonEmployees.EmployeeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalonEmployeeResponse {

    private Long employeeRecordId;

    private Long userId;

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private String phoneNumber;

    private EmployeeStatus status;

    private LocalDateTime joinedAt;
}