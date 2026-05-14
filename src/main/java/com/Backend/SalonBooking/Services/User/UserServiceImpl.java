package com.Backend.SalonBooking.Services.User;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Entities.Users.User;
import com.Backend.SalonBooking.Repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;

    @Override
    public ApiResponse<User> createUser(User user) {
        userRepo.save(user);
        return ApiResponse.success("User created successfully", user);
    }

    @Override
    public ApiResponse<User> getUserById(long id) {
        Optional<User> user=userRepo.findById(id);
        if(user.isPresent()) {
            return ApiResponse.success("User found successfully", user.get());
        }

        return ApiResponse.error("User not found");
    }

    @Override
    public ApiResponse<User> getUserByEmail(String email) {
       Optional<User> user=userRepo.findByEmail(email);
        if(user.isPresent()) {
        return ApiResponse.success("User successfully retrieved", user.get());
        }
        return ApiResponse.error("User not found");

    }

    @Override
    public ApiResponse<List<User>> getAllUsers() {
        return ApiResponse.success("Users Fetched Successfully",userRepo.findAll());
    }



    @Override
    public ApiResponse<User> deleteUser(long id) {
       userRepo.deleteById(id);
       return ApiResponse.success("User deleted successfully", userRepo.findById(id).get());
    }
}
