package com.Backend.SalonBooking.Services.Salon.Hiring;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.HiringEmps.CreateHirePostRequest;
import com.Backend.SalonBooking.Dtos.HiringEmps.HirePostResponse;
import com.Backend.SalonBooking.Entities.HiringPost.HirePost;
import com.Backend.SalonBooking.Entities.HiringPost.HiringStatus;
import com.Backend.SalonBooking.Entities.SalonEmployees.EmployeeStatus;
import com.Backend.SalonBooking.Entities.SalonEmployees.Salonemps;
import com.Backend.SalonBooking.Entities.Salons.Salon;
import com.Backend.SalonBooking.Entities.Users.User;
import com.Backend.SalonBooking.Repositories.SalonEmployeesRepo;
import com.Backend.SalonBooking.Repositories.SalonHiringPostRepo;
import com.Backend.SalonBooking.Repositories.SalonRepo;
import com.Backend.SalonBooking.Repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalonHiringServiceImpl implements SalonHiringService {

    private final SalonRepo salonRepo;
    private final UserRepo userRepo;
    private final SalonHiringPostRepo salonHiringPostRepo;
    private final SalonEmployeesRepo salonEmployeesRepo;

    @Override
    @Transactional
    public ApiResponse<HirePostResponse> createHirePost(CreateHirePostRequest request) {

        User owner = getCurrentUser();

        Salon salon = salonRepo.findByOwnerId(owner.getId())
                .orElse(null);

        if (salon == null) {
            return ApiResponse.error("You do not have a salon");
        }

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            return ApiResponse.error("Title is required");
        }

        if (request.getDescription() == null || request.getDescription().isBlank()) {
            return ApiResponse.error("Description is required");
        }

        if (request.getNumOfEmps() <= 0) {
            return ApiResponse.error("Number of employees must be greater than 0");
        }

        long currentEmployees = salonEmployeesRepo.countBySalonIdAndStatus(
                salon.getId(),
                EmployeeStatus.ACTIVE
        );

        long availableSlots = salon.getMaxNumOfEmployees() - currentEmployees;

        if (availableSlots <= 0) {
            return ApiResponse.error("Salon has no available employee slots");
        }

        if (request.getNumOfEmps() > availableSlots) {
            return ApiResponse.error("Requested employees number is greater than available slots");
        }

        HirePost post = new HirePost();

        post.setTitle(request.getTitle().trim());
        post.setDescription(request.getDescription().trim());
        post.setNeededEmployees(request.getNumOfEmps());
        post.setStatus(HiringStatus.Available);
        post.setCreatedAt(LocalDateTime.now());
        post.setCity(salon.getCity());
        post.setSalon(salon);

        HirePost savedPost = salonHiringPostRepo.save(post);

        return ApiResponse.success("Hiring post created successfully", mapToResponse(savedPost));
    }

    @Override
    @Transactional
    public ApiResponse<String> joinHiringSalon(Long postId) {

        User currentUser = getCurrentUser();

        HirePost post = salonHiringPostRepo.findById(postId)
                .orElse(null);

        if (post == null) {
            return ApiResponse.error("Hiring post not found");
        }

        if (post.getStatus() != HiringStatus.Available) {
            return ApiResponse.error("This hiring post is closed");
        }

        Salon salon = post.getSalon();

        if (salon == null) {
            return ApiResponse.error("Salon not found for this hiring post");
        }

        if (salon.getOwner() != null && salon.getOwner().getId().equals(currentUser.getId())) {
            return ApiResponse.error("Salon owner cannot join his own salon as employee");
        }

        boolean alreadyEmployee = salonEmployeesRepo.existsByEmployeeIdAndStatus(
                currentUser.getId(),
                EmployeeStatus.ACTIVE
        );

        if (alreadyEmployee) {
            return ApiResponse.error("You are already employee in another salon");
        }

        long currentEmployees = salonEmployeesRepo.countBySalonIdAndStatus(
                salon.getId(),
                EmployeeStatus.ACTIVE
        );

        if (currentEmployees >= salon.getMaxNumOfEmployees()) {
            post.setStatus(HiringStatus.Unavailable);
            salonHiringPostRepo.save(post);

            return ApiResponse.error("Salon reached maximum number of employees");
        }

        if (post.getNeededEmployees() <= 0) {
            post.setStatus(HiringStatus.Unavailable);
            salonHiringPostRepo.save(post);

            return ApiResponse.error("This hiring post does not need employees anymore");
        }

        Salonemps employee = new Salonemps();

        employee.setSalon(salon);
        employee.setEmployee(currentUser);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setJoinedAt(LocalDateTime.now());
        employee.setLeftAt(null);

        salonEmployeesRepo.save(employee);

        salon.setCurrentNumOfEmployees((int) currentEmployees + 1);
        salonRepo.save(salon);

        post.setNeededEmployees(post.getNeededEmployees() - 1);

        if (post.getNeededEmployees() <= 0) {
            post.setStatus(HiringStatus.Unavailable);
        }

        salonHiringPostRepo.save(post);

        return ApiResponse.success("You joined the salon successfully", null);
    }

    @Override
    public ApiResponse<List<HirePostResponse>> getOpenHiringPosts() {

        List<HirePostResponse> response = salonHiringPostRepo.findByStatus(HiringStatus.Available)
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ApiResponse.success("Open hiring posts returned successfully", response);
    }

    @Override
    @Transactional
    public ApiResponse<String> closeHiringPost(Long postId) {

        User owner = getCurrentUser();

        HirePost post = salonHiringPostRepo.findById(postId)
                .orElse(null);

        if (post == null) {
            return ApiResponse.error("Hiring post not found");
        }

        Salon salon = post.getSalon();

        if (salon == null || salon.getOwner() == null) {
            return ApiResponse.error("Salon owner not found");
        }

        if (!salon.getOwner().getId().equals(owner.getId())) {
            return ApiResponse.error("You are not allowed to close this hiring post");
        }

        post.setStatus(HiringStatus.Unavailable);
        salonHiringPostRepo.save(post);

        return ApiResponse.success("Hiring post closed successfully", null);
    }

    private User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepo.findByEmail(email)
                .orElseThrow();
    }

    private HirePostResponse mapToResponse(HirePost post) {

        HirePostResponse response = new HirePostResponse();

        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setDescription(post.getDescription());
        response.setNeededEmployees(post.getNeededEmployees());
        response.setStatus(post.getStatus());
        response.setCreatedAt(post.getCreatedAt());

        if (post.getSalon() != null) {
            response.setSalonId(post.getSalon().getId());
            response.setSalonName(post.getSalon().getName());
            response.setSalonCity(post.getSalon().getCity());
        }

        return response;
    }
}
