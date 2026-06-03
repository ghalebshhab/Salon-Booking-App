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
public class CreateSalonResponse {

    private Long salonId;

    private Long ownerId;
    private String ownerName;
    private String ownerEmail;
    private String ownerPhoneNumber;

    private String name;
    private String email;
    private String address;
    private String phoneNumber;

    private LocalTime openTime;
    private LocalTime closeTime;

    private String city;
    private State state;

    private boolean isDeleted = false;

    private List<String> Images;

    private int maxNumOfEmployees;
    private int currentNumOfEmployees;
}
