package com.Backend.SalonBooking.Controllers.Salon;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.SalonServices.CreateSalonServiceRequest;
import com.Backend.SalonBooking.Dtos.SalonServices.SalonServiceResponse;
import com.Backend.SalonBooking.Dtos.SalonServices.UpdateSalonServiceRequest;
import com.Backend.SalonBooking.Services.SalonServices.SalonServiceItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/salon-services")
@RequiredArgsConstructor
public class SalonServiceItemController {

    private final SalonServiceItemService salonServiceItemService;

    @PostMapping
    public ResponseEntity<ApiResponse<SalonServiceResponse>> createService(
            @RequestBody CreateSalonServiceRequest request
    ) {
        return ResponseEntity.ok(salonServiceItemService.createService(request));
    }

    @GetMapping("/salon/{salonId}")
    public ResponseEntity<ApiResponse<List<SalonServiceResponse>>> getActiveServicesBySalon(
            @PathVariable Long salonId
    ) {
        return ResponseEntity.ok(salonServiceItemService.getActiveServicesBySalon(salonId));
    }

    @GetMapping("/my-salon")
    public ResponseEntity<ApiResponse<List<SalonServiceResponse>>> getMySalonServices() {
        return ResponseEntity.ok(salonServiceItemService.getMySalonServices());
    }

    @PutMapping("/{serviceId}")
    public ResponseEntity<ApiResponse<SalonServiceResponse>> updateService(
            @PathVariable Long serviceId,
            @RequestBody UpdateSalonServiceRequest request
    ) {
        return ResponseEntity.ok(salonServiceItemService.updateService(serviceId, request));
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<ApiResponse<String>> deleteService(
            @PathVariable Long serviceId
    ) {
        return ResponseEntity.ok(salonServiceItemService.deleteService(serviceId));
    }
}