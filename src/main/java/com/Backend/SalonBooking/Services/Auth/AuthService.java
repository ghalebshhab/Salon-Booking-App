package com.Backend.SalonBooking.Services.Auth;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.Auth.Login.LoginRequest;
import com.Backend.SalonBooking.Dtos.Auth.Login.LoginResponse;
import com.Backend.SalonBooking.Dtos.Auth.Rigester.RigesterRequest;
import com.Backend.SalonBooking.Dtos.Auth.Rigester.RigesterResponse;
import com.Backend.SalonBooking.Entities.Users.User;

public interface AuthService {
ApiResponse<RigesterResponse> Rigester(RigesterRequest request);
ApiResponse<LoginResponse> Login(LoginRequest loginRequest);

}
