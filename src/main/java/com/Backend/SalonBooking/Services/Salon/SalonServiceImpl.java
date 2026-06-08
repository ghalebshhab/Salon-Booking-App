package com.Backend.SalonBooking.Services.Salon;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.Salons.CreateSalonRequest;
import com.Backend.SalonBooking.Dtos.Salons.CreateSalonResponse;
import com.Backend.SalonBooking.Dtos.Salons.SalonEmployeeResponse;
import com.Backend.SalonBooking.Dtos.Salons.UpdateSalonInfoRequest;
import com.Backend.SalonBooking.Entities.SalonEmployees.EmployeeStatus;
import com.Backend.SalonBooking.Entities.SalonEmployees.Salonemps;
import com.Backend.SalonBooking.Entities.Salons.Salon;
import com.Backend.SalonBooking.Entities.Salons.State;
import com.Backend.SalonBooking.Entities.Users.Role;
import com.Backend.SalonBooking.Entities.Users.User;
import com.Backend.SalonBooking.Repositories.SalonEmployeesRepo;
import com.Backend.SalonBooking.Repositories.SalonRepo;
import com.Backend.SalonBooking.Repositories.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.Backend.SalonBooking.Dtos.Salons.CreateSalonEmployeeRequest;
import com.Backend.SalonBooking.Entities.SalonEmployees.Salonemps;
import com.Backend.SalonBooking.Services.SalonEmployees.SalonEmployeeServiceImpl;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SalonServiceImpl implements SalonService {

    private final UserRepo userRepo;
    private final SalonRepo salonRepo;
    private final SalonEmployeesRepo salonEmployeesRepo;
    private final SalonEmployeeServiceImpl salonEmployeeService;

    @Override
    public ApiResponse<CreateSalonResponse> createSalon(CreateSalonRequest createSalonRequest, String emailFromToken) {

        if (createSalonRequest.getName() == null || createSalonRequest.getName().isBlank()) {
            return ApiResponse.error("Name is required");
        }

        if (createSalonRequest.getCity() == null || createSalonRequest.getCity().isBlank()) {
            return ApiResponse.error("City is required");
        }

        if (createSalonRequest.getOpenTime() == null) {
            return ApiResponse.error("Open time is required");
        }

        if (createSalonRequest.getCloseTime() == null) {
            return ApiResponse.error("Close time is required");
        }

        if (!createSalonRequest.getOpenTime().isBefore(createSalonRequest.getCloseTime())) {
            return ApiResponse.error("Open time must be before close time");
        }

        if (createSalonRequest.getAddress() == null || createSalonRequest.getAddress().isBlank()) {
            return ApiResponse.error("Address is required");
        }

        if (createSalonRequest.getEmail() == null || createSalonRequest.getEmail().isBlank()) {
            return ApiResponse.error("Email is required");
        }

        if (createSalonRequest.getPhoneNumber() == null ||
                (!createSalonRequest.getPhoneNumber().matches("^\\+9627\\d{8}$")
                        && !createSalonRequest.getPhoneNumber().matches("^07\\d{8}$"))) {
            return ApiResponse.error("Phone number must be like +9627XXXXXXXX or 07XXXXXXXX");
        }

        if (createSalonRequest.getMaxNumOfEmployees() == null ||
                createSalonRequest.getMaxNumOfEmployees() <= 0) {
            return ApiResponse.error("Max number of employees is required and must be greater than 0");
        }

        List<CreateSalonEmployeeRequest> employeeRequests =
                createSalonRequest.getEmployees() == null
                        ? List.of()
                        : createSalonRequest.getEmployees();

        if (employeeRequests.size() > createSalonRequest.getMaxNumOfEmployees()) {
            return ApiResponse.error("Employees list cannot be greater than max employees");
        }

        User user = userRepo.findByEmail(emailFromToken).orElse(null);

        if (user == null) {
            return ApiResponse.error("User With This Email not found");
        }

        if (salonRepo.existsByOwnerId(user.getId())) {
            return ApiResponse.error("You already have a salon");
        }

        Salon salon = new Salon();

        salon.setCity(createSalonRequest.getCity());
        salon.setAddress(createSalonRequest.getAddress());
        salon.setEmail(createSalonRequest.getEmail());
        salon.setPhoneNumber(createSalonRequest.getPhoneNumber());
        salon.setName(createSalonRequest.getName());
        salon.setOpenTime(createSalonRequest.getOpenTime());
        salon.setCloseTime(createSalonRequest.getCloseTime());
        salon.setState(calculateState(createSalonRequest.getOpenTime(), createSalonRequest.getCloseTime()));
        salon.setOwner(user);
        salon.setImages(createSalonRequest.getImages());
        salon.setMaxNumOfEmployees(createSalonRequest.getMaxNumOfEmployees());
        salon.setCurrentNumOfEmployees(employeeRequests.size());

        user.setRole(Role.OWNER);
        userRepo.save(user);

        Salon savedSalon = salonRepo.save(salon);

        for (CreateSalonEmployeeRequest employeeRequest : employeeRequests) {

            ApiResponse<String> validation = salonEmployeeService.validateEmployeeRequest(employeeRequest, savedSalon);

            if (!validation.isSuccess()) {
                return ApiResponse.error(validation.getMessage());
            }

            Salonemps employee = salonEmployeeService.buildInvitedEmployee(savedSalon, employeeRequest);
            Salonemps savedEmployee = salonEmployeesRepo.save(employee);
            salonEmployeeService.sendInvitationAndUpdate(savedEmployee);
        }

        CreateSalonResponse response = toResponse(savedSalon);

        return ApiResponse.success("Salon Created Successfully", response);
    }
    @Override
    public ApiResponse<CreateSalonResponse> updateSalon(UpdateSalonInfoRequest updateSalonInfoRequest,
                        String emailFromToken,
                        Long salonId) {
    User user=userRepo.findByEmail(emailFromToken).orElse(null);
    if(user==null){
        return ApiResponse.error("User not found");
    }
    Salon salon=salonRepo.findById(salonId).orElse(null);
     if (salon==null){
         return ApiResponse.error("Salon not found");
     }
     if(!salon.getOwner().getId().equals(user.getId())){
         return ApiResponse.error("You Can Only Update Your Own Salon");
     }
        if(updateSalonInfoRequest.getName()==null){
            return ApiResponse.error("Name is required");
        }
        if(updateSalonInfoRequest.getCity()==null){
            return ApiResponse.error("City is required");
        }
        if(updateSalonInfoRequest.getOpenTime()==null){
            return ApiResponse.error("Open time is required");
        }
        if (updateSalonInfoRequest.getCloseTime()==null){
            return ApiResponse.error("Close time is required");
        }
        if (updateSalonInfoRequest.getAddress()==null){
            return ApiResponse.error("Address is required");
        }
        if (updateSalonInfoRequest.getEmail()==null){
            return ApiResponse.error("Email is required");
        }
        if (updateSalonInfoRequest.getPhoneNumber() == null ||
                (!updateSalonInfoRequest.getPhoneNumber().matches("^\\+9627\\d{8}$")
                        && !updateSalonInfoRequest.getPhoneNumber().matches("^07\\d{8}$"))) {
            return ApiResponse.error("Phone number must be like +9627XXXXXXXX or 07XXXXXXXX");
        }
        salon.setName(updateSalonInfoRequest.getName());
        salon.setOpenTime(updateSalonInfoRequest.getOpenTime());
        salon.setCloseTime(updateSalonInfoRequest.getCloseTime());
        salon.setAddress(updateSalonInfoRequest.getAddress());
        salon.setEmail(updateSalonInfoRequest.getEmail());
        salon.setPhoneNumber(updateSalonInfoRequest.getPhoneNumber());
        salon.setImages(updateSalonInfoRequest.getImages());
        salon.setCity(updateSalonInfoRequest.getCity());
        salon.setState(calculateState(updateSalonInfoRequest.getOpenTime(),updateSalonInfoRequest.getCloseTime()));
        salon.setCurrentNumOfEmployees(updateSalonInfoRequest.getCurrentNumOfEmployees());
        salon.setMaxNumOfEmployees(updateSalonInfoRequest.getMaxNumOfEmployees());
        salonRepo.save(salon);

        CreateSalonResponse response=toResponse(salon);

        return ApiResponse.success("Salon Information Updated Successfully", response);
    }

    @Override
    public ApiResponse<String> deleteSalon(String emailFromToken) {
    User user=userRepo.findByEmail(emailFromToken).orElse(null);
    if(user==null){
        return ApiResponse.error("User not found");
    }
    Salon salon=salonRepo.findByOwner(user).orElse(null);
    if(salon==null){
        return ApiResponse.error("Salon not found");
    }
    salon.setIsDeleted(true);
    salonRepo.save(salon);

        return ApiResponse.success("Salon Deleted Successfully",null);
    }
    @Override
    public ApiResponse<List<SalonEmployeeResponse>> getSalonEmployees(Long salonId) {

        Salon salon = salonRepo.findById(salonId)
                .orElse(null);

        if (salon == null) {
            return ApiResponse.error("Salon not found");
        }

        List<Salonemps> employees = salonEmployeesRepo.findBySalonIdAndStatus(
                salonId,
                EmployeeStatus.ACTIVE
        );

        List<SalonEmployeeResponse> response = employees.stream()
                .map(this::mapToSalonEmployeeResponse)
                .toList();

        return ApiResponse.success("Salon employees returned successfully", response);
    }

    @Override
    public ApiResponse<CreateSalonResponse> getSalonById(Long salonId) {
        Salon salon=salonRepo.findById(salonId).orElse(null);
        if(salon==null){
            return ApiResponse.error("Salon not found");
        }


        return ApiResponse.success("Salon Fetched Successfully", toResponse(salon));
    }
    @Override
    public ApiResponse<CreateSalonResponse> getMySalon(String emailFromToken) {

        User user = userRepo.findByEmail(emailFromToken).orElse(null);

        if (user == null) {
            return ApiResponse.error("User not found");
        }

        Salon salon = salonRepo.findByOwner(user).orElse(null);

        if (salon == null) {
            return ApiResponse.error("Salon not found for this owner");
        }

        return ApiResponse.success("My salon returned successfully", toResponse(salon));
    }
    @Override
    public ApiResponse<List<CreateSalonResponse>> getAllPublicSalons() {

        List<CreateSalonResponse> response = salonRepo.findByIsDeletedFalse()
                .stream()
                .map(this::toResponse)
                .toList();

        return ApiResponse.success("Salons fetched successfully", response);
    }


    private CreateSalonResponse toResponse(Salon salon) {

        CreateSalonResponse response = new CreateSalonResponse();

        response.setSalonId(salon.getId());

        if (salon.getOwner() != null) {
            User owner = salon.getOwner();

            response.setOwnerId(owner.getId());

            String ownerName =
                    ((owner.getFirstName() == null ? "" : owner.getFirstName()) + " " +
                            (owner.getLastName() == null ? "" : owner.getLastName())).trim();

            if (ownerName.isBlank()) {
                ownerName = owner.getUsername();
            }

            response.setOwnerName(ownerName);
            response.setOwnerEmail(owner.getEmail());
            response.setOwnerPhoneNumber(owner.getPhoneNumber());
        }

        response.setName(salon.getName());
        response.setEmail(salon.getEmail());
        response.setCity(salon.getCity());
        response.setAddress(salon.getAddress());
        response.setPhoneNumber(salon.getPhoneNumber());

        response.setOpenTime(salon.getOpenTime());
        response.setCloseTime(salon.getCloseTime());
        response.setState(salon.getState());

        response.setImages(salon.getImages());

        response.setDeleted(salon.getIsDeleted());

        response.setCurrentNumOfEmployees(salon.getCurrentNumOfEmployees());
        response.setMaxNumOfEmployees(salon.getMaxNumOfEmployees());

        return response;
    }
    private State calculateState(LocalTime openTime, LocalTime closeTime) {

        LocalTime now = LocalTime.now();

        boolean isOpen =
                !now.isBefore(openTime) &&
                        now.isBefore(closeTime);

        return isOpen ? State.OPEN : State.CLOSED;
    }
    private SalonEmployeeResponse mapToSalonEmployeeResponse(Salonemps salonEmployee) {

        User user = salonEmployee.getEmployee();

        SalonEmployeeResponse response = new SalonEmployeeResponse();

        response.setId(salonEmployee.getId());
        response.setStatus(salonEmployee.getStatus());
        response.setJoinedAt(salonEmployee.getJoinedAt());

        if (user != null) {
            response.setUserId(user.getId());
           response.setFullName(user.getFirstName() + " " + user.getLastName());
            response.setEmail(user.getEmail());
            response.setPhoneNumber(user.getPhoneNumber());
        }

        return response;
    }


}
