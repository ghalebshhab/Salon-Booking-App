package com.Backend.SalonBooking.Dtos.Salons;

import com.Backend.SalonBooking.Entities.Salons.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSalonRequest {

    private String name;

    private String email;

    private String address;

    private String phoneNumber;

    private LocalTime openTime;

    private LocalTime closeTime;

    private String city;

    private State state;

    private List<String> Images;

    private Integer maxNumOfEmployees;

    private Integer currentNumOfEmployees;

    private List<CreateSalonEmployeeRequest> employees;
}
