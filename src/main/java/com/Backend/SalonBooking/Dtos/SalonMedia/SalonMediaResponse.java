package com.Backend.SalonBooking.Dtos.SalonMedia;

import com.Backend.SalonBooking.Entities.SalonProfileMedia.MediaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalonMediaResponse {

    private Long id;

    private String caption;

    private String mediaUrl;

    private MediaType mediaType;

    private LocalDateTime createdAt;

    private Long salonId;

    private String salonName;
}