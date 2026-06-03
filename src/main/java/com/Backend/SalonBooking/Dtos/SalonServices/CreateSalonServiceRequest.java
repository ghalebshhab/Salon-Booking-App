package com.Backend.SalonBooking.Dtos.SalonServices;

import lombok.Data;

@Data
public class CreateSalonServiceRequest {

    private String name;

    private String description;

    private double price;

    private int durationMinutes;
}