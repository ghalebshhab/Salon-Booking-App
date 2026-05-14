package com.Backend.SalonBooking.Services.User;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Entities.Users.User;

import java.util.List;

public interface UserService {
ApiResponse<User> createUser(User user);

ApiResponse<User> getUserById(long id);
ApiResponse<User> getUserByEmail(String email);
ApiResponse<List<User>> getAllUsers();


ApiResponse<User> deleteUser(long id);


}
