package com.Backend.SalonBooking.Dtos.HiringEmps;

import com.Backend.SalonBooking.Entities.HiringPost.HiringStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HirePostResponse {

    private Long id;

    private String title;

    private String description;

    private Integer neededEmployees;

    private HiringStatus status;

    private LocalDateTime createdAt;

    private Long salonId;

    private String salonName;

    private String salonCity;

}
