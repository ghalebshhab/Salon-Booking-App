package com.Backend.SalonBooking.Services.Salon;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.Salons.CreateSalonRequest;
import com.Backend.SalonBooking.Dtos.Salons.CreateSalonResponse;
import com.Backend.SalonBooking.Dtos.Salons.UpdateSalonInfoRequest;
import com.Backend.SalonBooking.Entities.Salons.Salon;

public interface SalonService {
    ApiResponse<CreateSalonResponse> createSalon(CreateSalonRequest createSalonRequest,String emailFromToken);
    ApiResponse<CreateSalonResponse> updateSalon(UpdateSalonInfoRequest updateSalonInfoRequest
    ,String emailFromToken
            ,Long salonId);
    ApiResponse<String>  deleteSalon(String emailFromToken);
    ApiResponse<CreateSalonResponse> getSalonById(Long salonId);
    

}
