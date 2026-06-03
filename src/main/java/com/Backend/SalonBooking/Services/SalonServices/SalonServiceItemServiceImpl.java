package com.Backend.SalonBooking.Services.SalonServices;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.SalonServices.CreateSalonServiceRequest;
import com.Backend.SalonBooking.Dtos.SalonServices.SalonServiceResponse;
import com.Backend.SalonBooking.Dtos.SalonServices.UpdateSalonServiceRequest;
import com.Backend.SalonBooking.Entities.SalonServices.SalonServicesItem;
import com.Backend.SalonBooking.Entities.Salons.Salon;
import com.Backend.SalonBooking.Entities.Users.User;
import com.Backend.SalonBooking.Repositories.SalonRepo;
import com.Backend.SalonBooking.Repositories.SalonServiceItemRepo;
import com.Backend.SalonBooking.Repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalonServiceItemServiceImpl implements SalonServiceItemService {

    private final SalonServiceItemRepo salonServiceItemRepo;
    private final SalonRepo salonRepo;
    private final UserRepo userRepo;

    @Override
    @Transactional
    public ApiResponse<SalonServiceResponse> createService(CreateSalonServiceRequest request) {

        User owner = getCurrentUser();

        Salon salon = salonRepo.findByOwnerId(owner.getId()).orElse(null);

        if (salon == null) {
            return ApiResponse.error("You do not have a salon");
        }

        if (salon.getIsDeleted()) {
            return ApiResponse.error("Your salon is deleted");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            return ApiResponse.error("Service name is required");
        }

        if (request.getPrice() <= 0) {
            return ApiResponse.error("Price must be greater than 0");
        }

        if (request.getDurationMinutes() <= 0) {
            return ApiResponse.error("Duration must be greater than 0");
        }

        SalonServicesItem serviceItem = new SalonServicesItem();

        serviceItem.setName(request.getName().trim());
        serviceItem.setDescription(request.getDescription());
        serviceItem.setPrice(request.getPrice());
        serviceItem.setDurationMinutes(request.getDurationMinutes());
        serviceItem.setIsActive(true);
        serviceItem.setSalon(salon);

        SalonServicesItem saved = salonServiceItemRepo.save(serviceItem);

        return ApiResponse.success("Salon service created successfully", mapToResponse(saved));
    }

    @Override
    public ApiResponse<List<SalonServiceResponse>> getActiveServicesBySalon(Long salonId) {

        Salon salon = salonRepo.findById(salonId).orElse(null);

        if (salon == null) {
            return ApiResponse.error("Salon not found");
        }

        if (salon.getIsDeleted()) {
            return ApiResponse.error("Salon is deleted");
        }

        List<SalonServiceResponse> response = salonServiceItemRepo
                .findBySalonIdAndIsActiveTrue(salonId)
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ApiResponse.success("Salon services returned successfully", response);
    }

    @Override
    public ApiResponse<List<SalonServiceResponse>> getMySalonServices() {

        User owner = getCurrentUser();

        Salon salon = salonRepo.findByOwnerId(owner.getId()).orElse(null);

        if (salon == null) {
            return ApiResponse.error("You do not have a salon");
        }

        List<SalonServiceResponse> response = salonServiceItemRepo
                .findBySalonId(salon.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ApiResponse.success("My salon services returned successfully", response);
    }

    @Override
    @Transactional
    public ApiResponse<SalonServiceResponse> updateService(Long serviceId, UpdateSalonServiceRequest request) {

        User owner = getCurrentUser();

        SalonServicesItem serviceItem = salonServiceItemRepo.findById(serviceId).orElse(null);

        if (serviceItem == null) {
            return ApiResponse.error("Service not found");
        }

        Salon salon = serviceItem.getSalon();

        if (salon == null || salon.getOwner() == null) {
            return ApiResponse.error("Salon owner not found");
        }

        if (!salon.getOwner().getId().equals(owner.getId())) {
            return ApiResponse.error("You can only update your own salon services");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            return ApiResponse.error("Service name is required");
        }

        if (request.getPrice() <= 0) {
            return ApiResponse.error("Price must be greater than 0");
        }

        if (request.getDurationMinutes() <= 0) {
            return ApiResponse.error("Duration must be greater than 0");
        }

        serviceItem.setName(request.getName().trim());
        serviceItem.setDescription(request.getDescription());
        serviceItem.setPrice(request.getPrice());
        serviceItem.setDurationMinutes(request.getDurationMinutes());

        if (request.getIsActive() != null) {
            serviceItem.setIsActive(request.getIsActive());
        }

        SalonServicesItem saved = salonServiceItemRepo.save(serviceItem);

        return ApiResponse.success("Salon service updated successfully", mapToResponse(saved));
    }

    @Override
    @Transactional
    public ApiResponse<String> deleteService(Long serviceId) {

        User owner = getCurrentUser();

        SalonServicesItem serviceItem = salonServiceItemRepo.findById(serviceId).orElse(null);

        if (serviceItem == null) {
            return ApiResponse.error("Service not found");
        }

        Salon salon = serviceItem.getSalon();

        if (salon == null || salon.getOwner() == null) {
            return ApiResponse.error("Salon owner not found");
        }

        if (!salon.getOwner().getId().equals(owner.getId())) {
            return ApiResponse.error("You can only delete your own salon services");
        }

        serviceItem.setIsActive(false);
        salonServiceItemRepo.save(serviceItem);

        return ApiResponse.success("Salon service deleted successfully", null);
    }

    private User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    private SalonServiceResponse mapToResponse(SalonServicesItem serviceItem) {

        SalonServiceResponse response = new SalonServiceResponse();

        response.setId(serviceItem.getId());
        response.setName(serviceItem.getName());
        response.setDescription(serviceItem.getDescription());
        response.setPrice(serviceItem.getPrice());
        response.setDurationMinutes(serviceItem.getDurationMinutes());
        response.setIsActive(serviceItem.getIsActive());

        if (serviceItem.getSalon() != null) {
            response.setSalonId(serviceItem.getSalon().getId());
            response.setSalonName(serviceItem.getSalon().getName());
        }

        return response;
    }
}