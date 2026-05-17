package com.Backend.SalonBooking.Dtos.SalonMedia;

import com.Backend.SalonBooking.Entities.SalonProfileMedia.MediaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSalonMediaRequest {

    private String caption;

    private String mediaUrl;

    private MediaType mediaType;
}