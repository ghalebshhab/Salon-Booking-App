package com.Backend.SalonBooking.Dtos.HiringEmps;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateHirePostRequest {

    private String title;

    private String description;

    private int numOfEmps;
}
