package com.Backend.SalonBooking.Services.SalonEmployees;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.Salons.CreateSalonEmployeeRequest;
import com.Backend.SalonBooking.Dtos.Salons.SalonEmployeeResponse;
import com.Backend.SalonBooking.Entities.SalonEmployees.EmployeeStatus;
import com.Backend.SalonBooking.Entities.SalonEmployees.Salonemps;
import com.Backend.SalonBooking.Entities.Salons.Salon;
import com.Backend.SalonBooking.Entities.Users.User;
import com.Backend.SalonBooking.Repositories.SalonEmployeesRepo;
import com.Backend.SalonBooking.Repositories.SalonRepo;
import com.Backend.SalonBooking.Repositories.UserRepo;
import com.Backend.SalonBooking.Services.Email.EmployeeInvitationEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalonEmployeeServiceImpl implements SalonEmployeeService {

    private final SalonEmployeesRepo salonEmployeesRepo;
    private final SalonRepo salonRepo;
    private final UserRepo userRepo;
    private final EmployeeInvitationEmailService invitationEmailService;

    @Override
    public ApiResponse<List<SalonEmployeeResponse>> getMySalonEmployees() {

        User owner = getCurrentUser();

        Salon salon = salonRepo.findByOwnerId(owner.getId()).orElse(null);

        if (salon == null) {
            return ApiResponse.error("You do not have a salon");
        }

        List<SalonEmployeeResponse> response = salonEmployeesRepo
                .findBySalonIdAndStatusIn(
                        salon.getId(),
                        List.of(EmployeeStatus.INVITED, EmployeeStatus.ACTIVE, EmployeeStatus.INACTIVE)
                )
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ApiResponse.success("My salon employees returned successfully", response);
    }

    @Override
    public ApiResponse<List<SalonEmployeeResponse>> getSalonEmployees(Long salonId) {

        Salon salon = salonRepo.findById(salonId).orElse(null);

        if (salon == null) {
            return ApiResponse.error("Salon not found");
        }

        List<SalonEmployeeResponse> response = salonEmployeesRepo
                .findBySalonIdAndStatusIn(
                        salonId,
                        List.of(EmployeeStatus.INVITED, EmployeeStatus.ACTIVE)
                )
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ApiResponse.success("Salon employees returned successfully", response);
    }

    @Override
    @Transactional
    public ApiResponse<SalonEmployeeResponse> addEmployeeToMySalon(CreateSalonEmployeeRequest request) {

        User owner = getCurrentUser();

        Salon salon = salonRepo.findByOwnerId(owner.getId()).orElse(null);

        if (salon == null) {
            return ApiResponse.error("You do not have a salon");
        }

        ApiResponse<String> validation = validateEmployeeRequest(request, salon);

        if (!validation.isSuccess()) {
            return ApiResponse.error(validation.getMessage());
        }

        long currentCount = salonEmployeesRepo
                .findBySalonIdAndStatusIn(
                        salon.getId(),
                        List.of(EmployeeStatus.INVITED, EmployeeStatus.ACTIVE, EmployeeStatus.INACTIVE)
                )
                .size();

        if (currentCount >= salon.getMaxNumOfEmployees()) {
            return ApiResponse.error("You reached the max number of employees");
        }

        Salonemps employee = buildInvitedEmployee(salon, request);

        Salonemps saved = salonEmployeesRepo.save(employee);

        sendInvitationAndUpdate(saved);

        salon.setCurrentNumOfEmployees((int) currentCount + 1);
        salonRepo.save(salon);

        return ApiResponse.success("Employee added and invitation sent", mapToResponse(saved));
    }

    @Override
    @Transactional
    public ApiResponse<String> resendInvitation(Long employeeId) {

        User owner = getCurrentUser();

        Salonemps employee = salonEmployeesRepo.findById(employeeId).orElse(null);

        if (employee == null) {
            return ApiResponse.error("Employee not found");
        }

        if (!isSalonOwner(owner, employee.getSalon())) {
            return ApiResponse.error("You can only resend invitations for your own salon");
        }

        if (employee.getStatus() == EmployeeStatus.ACTIVE) {
            return ApiResponse.error("Employee is already active");
        }

        boolean sent = invitationEmailService.sendEmployeeInvitation(employee);

        if (sent) {
            employee.setInvitationSent(true);
            employee.setInvitationSentAt(LocalDateTime.now());
            salonEmployeesRepo.save(employee);
            return ApiResponse.success("Invitation resent successfully", null);
        }

        return ApiResponse.error("Failed to send invitation");
    }

    @Override
    @Transactional
    public ApiResponse<String> removeEmployee(Long employeeId) {

        User owner = getCurrentUser();

        Salonemps employee = salonEmployeesRepo.findById(employeeId).orElse(null);

        if (employee == null) {
            return ApiResponse.error("Employee not found");
        }

        if (!isSalonOwner(owner, employee.getSalon())) {
            return ApiResponse.error("You can only remove employees from your own salon");
        }

        employee.setStatus(EmployeeStatus.LEFT);
        employee.setLeftAt(LocalDateTime.now());
        salonEmployeesRepo.save(employee);

        Salon salon = employee.getSalon();

        long count = salonEmployeesRepo
                .findBySalonIdAndStatusIn(
                        salon.getId(),
                        List.of(EmployeeStatus.INVITED, EmployeeStatus.ACTIVE, EmployeeStatus.INACTIVE)
                )
                .size();

        salon.setCurrentNumOfEmployees((int) count);
        salonRepo.save(salon);

        return ApiResponse.success("Employee removed successfully", null);
    }

    public Salonemps buildInvitedEmployee(Salon salon, CreateSalonEmployeeRequest request) {

        Salonemps employee = new Salonemps();

        employee.setSalon(salon);
        employee.setEmployee(null);
        employee.setFullName(request.getFullName().trim());
        employee.setEmail(request.getEmail().trim().toLowerCase());
        employee.setPhoneNumber(request.getPhoneNumber().trim());
        employee.setImageUrl(request.getImageUrl());
        employee.setSpecialty(request.getSpecialty());
        employee.setStartTime(request.getStartTime());
        employee.setEndTime(request.getEndTime());
        employee.setStatus(EmployeeStatus.INVITED);
        employee.setInvitationSent(false);

        return employee;
    }

    public void sendInvitationAndUpdate(Salonemps employee) {

        boolean sent = invitationEmailService.sendEmployeeInvitation(employee);

        if (sent) {
            employee.setInvitationSent(true);
            employee.setInvitationSentAt(LocalDateTime.now());
            salonEmployeesRepo.save(employee);
        }
    }

    public ApiResponse<String> validateEmployeeRequest(CreateSalonEmployeeRequest request, Salon salon) {

        if (request == null) {
            return ApiResponse.error("Employee data is required");
        }

        if (request.getFullName() == null || request.getFullName().isBlank()) {
            return ApiResponse.error("Employee name is required");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ApiResponse.error("Employee email is required");
        }

        if (request.getPhoneNumber() == null ||
                (!request.getPhoneNumber().matches("^\\+9627\\d{8}$")
                        && !request.getPhoneNumber().matches("^07\\d{8}$"))) {
            return ApiResponse.error("Employee phone number must be like +9627XXXXXXXX or 07XXXXXXXX");
        }

        if (request.getStartTime() == null) {
            return ApiResponse.error("Employee start time is required");
        }

        if (request.getEndTime() == null) {
            return ApiResponse.error("Employee end time is required");
        }

        if (!request.getStartTime().isBefore(request.getEndTime())) {
            return ApiResponse.error("Employee start time must be before end time");
        }

        if (request.getStartTime().isBefore(salon.getOpenTime()) ||
                request.getEndTime().isAfter(salon.getCloseTime())) {
            return ApiResponse.error("Employee working time must be inside salon working hours");
        }

        if (salonEmployeesRepo.existsBySalonIdAndEmailIgnoreCaseAndStatusIn(
                salon.getId(),
                request.getEmail().trim(),
                List.of(EmployeeStatus.INVITED, EmployeeStatus.ACTIVE, EmployeeStatus.INACTIVE)
        )) {
            return ApiResponse.error("Employee email already exists in this salon");
        }

        if (salonEmployeesRepo.existsBySalonIdAndPhoneNumberAndStatusIn(
                salon.getId(),
                request.getPhoneNumber().trim(),
                List.of(EmployeeStatus.INVITED, EmployeeStatus.ACTIVE, EmployeeStatus.INACTIVE)
        )) {
            return ApiResponse.error("Employee phone already exists in this salon");
        }

        return ApiResponse.success("Valid employee", null);
    }

    private User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepo.findByEmail(email).orElse(null);
    }

    private boolean isSalonOwner(User owner, Salon salon) {

        if (salon == null || salon.getOwner() == null) {
            return false;
        }

        return salon.getOwner().getId().equals(owner.getId());
    }

    private SalonEmployeeResponse mapToResponse(Salonemps employee) {

        SalonEmployeeResponse response = new SalonEmployeeResponse();

        response.setId(employee.getId());

        if (employee.getSalon() != null) {
            response.setSalonId(employee.getSalon().getId());
        }

        if (employee.getEmployee() != null) {
            response.setUserId(employee.getEmployee().getId());
        }

        response.setFullName(employee.getFullName());
        response.setEmail(employee.getEmail());
        response.setPhoneNumber(employee.getPhoneNumber());
        response.setImageUrl(employee.getImageUrl());
        response.setSpecialty(employee.getSpecialty());
        response.setStartTime(employee.getStartTime());
        response.setEndTime(employee.getEndTime());
        response.setStatus(employee.getStatus());
        response.setInvitationSent(employee.getInvitationSent());
        response.setInvitationSentAt(employee.getInvitationSentAt());
        response.setJoinedAt(employee.getJoinedAt());

        return response;
    }
}