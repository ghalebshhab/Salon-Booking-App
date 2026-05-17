package com.Backend.SalonBooking.Controllers.Auth;

import com.Backend.SalonBooking.Dtos.Auth.Login.LoginRequest;
import com.Backend.SalonBooking.Dtos.Auth.Rigester.RigesterRequest;
import com.Backend.SalonBooking.Repositories.UserRepo;
import com.Backend.SalonBooking.Services.Auth.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RigesterRequest request) {
        return ResponseEntity.ok(authService.Rigester(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.Login(request));

    }
}