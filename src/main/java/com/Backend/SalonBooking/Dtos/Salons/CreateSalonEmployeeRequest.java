package com.Backend.SalonBooking.Dtos.Salons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSalonEmployeeRequest {

    private String fullName;

    private String email;

    private String phoneNumber;

    private String imageUrl;

    private String specialty;

    private LocalTime startTime;

    private LocalTime endTime;
}