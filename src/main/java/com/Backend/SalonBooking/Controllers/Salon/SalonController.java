package com.Backend.SalonBooking.Controllers.Salon;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.Salons.CreateSalonRequest;
import com.Backend.SalonBooking.Dtos.Salons.CreateSalonResponse;
import com.Backend.SalonBooking.Dtos.Salons.SalonEmployeeResponse;
import com.Backend.SalonBooking.Dtos.Salons.UpdateSalonInfoRequest;
import com.Backend.SalonBooking.Repositories.SalonRepo;
import com.Backend.SalonBooking.Services.Salon.SalonService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salons")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"})
public class SalonController {

    private final SalonService salonService;
    private final SalonRepo salonRepo;

    @GetMapping("/{salonId}")
    public ApiResponse<CreateSalonResponse> getSalonById(@PathVariable Long salonId) {
        return salonService.getSalonById(salonId);
    }

    @GetMapping("/{salonId}/employees")
    public ApiResponse<List<SalonEmployeeResponse>> getSalonEmployees(
            @PathVariable Long salonId
    ) {
        return salonService.getSalonEmployees(salonId);
    }

    @GetMapping("/my-salon")
    public ApiResponse<CreateSalonResponse> getMySalon(Authentication authentication) {
        String emailFromToken = authentication.getName();
        return salonService.getMySalon(emailFromToken);
    }

    @PostMapping("/createSalon")
    public ApiResponse<CreateSalonResponse> createSalon(
            @RequestBody CreateSalonRequest createSalonRequest,
            Authentication authentication
    ) {
        String emailFromToken = authentication.getName();
        return salonService.createSalon(createSalonRequest, emailFromToken);
    }

    @PutMapping("/updateSalon/{salonId}")
    public ApiResponse<CreateSalonResponse> updateSalon(
            @RequestBody UpdateSalonInfoRequest updateSalonInfoRequest,
            @PathVariable Long salonId,
            Authentication authentication
    ) {
        String emailFromToken = authentication.getName();
        return salonService.updateSalon(updateSalonInfoRequest, emailFromToken, salonId);
    }

    @DeleteMapping("/deleteSalon")
    public ApiResponse<String> deleteSalon(Authentication authentication) {
        String emailFromToken = authentication.getName();
        return salonService.deleteSalon(emailFromToken);
    }
}
