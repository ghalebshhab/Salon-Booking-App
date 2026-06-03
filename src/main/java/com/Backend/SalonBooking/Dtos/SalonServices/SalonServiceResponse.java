package com.Backend.SalonBooking.Dtos.SalonServices;

import lombok.Data;

@Data
public class SalonServiceResponse {

    private Long id;

    private String name;

    private String description;

    private double price;

    private int durationMinutes;

    private Boolean isActive;

    private Long salonId;

    private String salonName;
}