package com.Backend.SalonBooking.Controllers.Salon;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.Salons.CreateSalonEmployeeRequest;
import com.Backend.SalonBooking.Dtos.Salons.SalonEmployeeResponse;
import com.Backend.SalonBooking.Services.SalonEmployees.SalonEmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"})
@RestController
@RequestMapping("/api/salon-employees")
@RequiredArgsConstructor
public class SalonEmployeeController {

    private final SalonEmployeeService salonEmployeeService;

    @GetMapping("/my-salon")
    public ResponseEntity<ApiResponse<List<SalonEmployeeResponse>>> getMySalonEmployees() {
        return ResponseEntity.ok(salonEmployeeService.getMySalonEmployees());
    }

    @GetMapping("/salon/{salonId}")
    public ResponseEntity<ApiResponse<List<SalonEmployeeResponse>>> getSalonEmployees(
            @PathVariable Long salonId
    ) {
        return ResponseEntity.ok(salonEmployeeService.getSalonEmployees(salonId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SalonEmployeeResponse>> addEmployeeToMySalon(
            @RequestBody CreateSalonEmployeeRequest request
    ) {
        return ResponseEntity.ok(salonEmployeeService.addEmployeeToMySalon(request));
    }

    @PostMapping("/{employeeId}/resend-invitation")
    public ResponseEntity<ApiResponse<String>> resendInvitation(
            @PathVariable Long employeeId
    ) {
        return ResponseEntity.ok(salonEmployeeService.resendInvitation(employeeId));
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<String>> removeEmployee(
            @PathVariable Long employeeId
    ) {
        return ResponseEntity.ok(salonEmployeeService.removeEmployee(employeeId));
    }
}