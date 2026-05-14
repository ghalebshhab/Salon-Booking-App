package com.Backend.SalonBooking.Services.Auth;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.Auth.Login.LoginRequest;
import com.Backend.SalonBooking.Dtos.Auth.Login.LoginResponse;
import com.Backend.SalonBooking.Dtos.Auth.Rigester.RigesterRequest;
import com.Backend.SalonBooking.Dtos.Auth.Rigester.RigesterResponse;
import com.Backend.SalonBooking.Entities.Users.Role;
import com.Backend.SalonBooking.Entities.Users.User;
import com.Backend.SalonBooking.Repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepo userRepo;
    @Override
    public ApiResponse<RigesterResponse> Rigester(RigesterRequest request) {

        if(userRepo.existsByEmail(request.getEmail())) {
            return ApiResponse.error("Email Already Exists");
        }
        if (request.getPhoneNumber() == null ||
                (!request.getPhoneNumber().matches("^\\+9627\\d{8}$")
                        && !request.getPhoneNumber().matches("^07\\d{8}$"))) {
            return ApiResponse.error("Phone number must be like +9627XXXXXXXX or 07XXXXXXXX");
        }
        if(userRepo.existsByUsername(request.getUserName())) {
            return ApiResponse.error("Username Already Exists");
        }
        if(userRepo.existsByPhoneNumber(request.getPhoneNumber())) {
            return ApiResponse.error("Phone Number Already Exists");
        }
        User user=new User();
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUserName());
        user.setRole(Role.USER);
        userRepo.save(user);
        return ApiResponse.success("Rigestered Successfully",new RigesterResponse());
    }

    @Override
    public ApiResponse<LoginResponse> Login(LoginRequest loginRequest) {
        return null;
    }
}
