package com.Backend.SalonBooking.Services.Salon.Hiring;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.HiringEmps.CreateHirePostRequest;
import com.Backend.SalonBooking.Dtos.HiringEmps.HirePostResponse;
import com.Backend.SalonBooking.Entities.Salons.Salon;
import com.Backend.SalonBooking.Entities.Users.User;
import com.Backend.SalonBooking.Repositories.SalonEmployeesRepo;
import com.Backend.SalonBooking.Repositories.SalonHiringPostRepo;
import com.Backend.SalonBooking.Repositories.SalonRepo;
import com.Backend.SalonBooking.Repositories.UserRepo;
import com.Backend.SalonBooking.Services.Salon.SalonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalonHiringServiceImpl implements SalonHiringService {

    private final SalonRepo salonRepo;
    private final UserRepo userRepo;
    private final SalonHiringPostRepo salonHiringPostRepo;
    private final SalonEmployeesRepo salonEmployeesRepo;

    @Override
    public ApiResponse<HirePostResponse> createHirePost(CreateHirePostRequest request) {
    Optional<Salon> opsalon=salonRepo.findById(request.getSalonId());
     if(opsalon.isEmpty()){
         return ApiResponse.error("Salon not found");
     }
     Salon salon=opsalon.get();





        return null;
    }

    @Override
    public ApiResponse<String> joinHiringSalon(Long postId) {
        return null;
    }

    @Override
    public ApiResponse<List<HirePostResponse>> getOpenHiringPosts() {
        return null;
    }

    @Override
    public ApiResponse<String> closeHiringPost(Long postId) {
        return null;
    }
}
