package com.Backend.SalonBooking.Services.SalonEmployees;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.Salons.CreateSalonEmployeeRequest;
import com.Backend.SalonBooking.Dtos.Salons.SalonEmployeeResponse;

import java.util.List;

public interface SalonEmployeeService {

    ApiResponse<List<SalonEmployeeResponse>> getMySalonEmployees();

    ApiResponse<List<SalonEmployeeResponse>> getSalonEmployees(Long salonId);

    ApiResponse<SalonEmployeeResponse> addEmployeeToMySalon(CreateSalonEmployeeRequest request);

    ApiResponse<String> resendInvitation(Long employeeId);

    ApiResponse<String> removeEmployee(Long employeeId);
}