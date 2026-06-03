package com.Backend.SalonBooking.Services.SalonServices;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.SalonServices.CreateSalonServiceRequest;
import com.Backend.SalonBooking.Dtos.SalonServices.SalonServiceResponse;
import com.Backend.SalonBooking.Dtos.SalonServices.UpdateSalonServiceRequest;

import java.util.List;

public interface SalonServiceItemService {

    ApiResponse<SalonServiceResponse> createService(CreateSalonServiceRequest request);

    ApiResponse<List<SalonServiceResponse>> getActiveServicesBySalon(Long salonId);

    ApiResponse<List<SalonServiceResponse>> getMySalonServices();

    ApiResponse<SalonServiceResponse> updateService(Long serviceId, UpdateSalonServiceRequest request);

    ApiResponse<String> deleteService(Long serviceId);
}