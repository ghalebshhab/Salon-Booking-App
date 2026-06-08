package com.Backend.SalonBooking.Services.Auth;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.Auth.Login.LoginRequest;
import com.Backend.SalonBooking.Dtos.Auth.Login.LoginResponse;
import com.Backend.SalonBooking.Dtos.Auth.Rigester.RigesterRequest;
import com.Backend.SalonBooking.Dtos.Auth.Rigester.RigesterResponse;
import com.Backend.SalonBooking.Entities.Users.Role;
import com.Backend.SalonBooking.Entities.Users.User;
import com.Backend.SalonBooking.Repositories.UserRepo;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import com.Backend.SalonBooking.Entities.SalonEmployees.EmployeeStatus;
import com.Backend.SalonBooking.Entities.SalonEmployees.Salonemps;
import com.Backend.SalonBooking.Repositories.SalonEmployeesRepo;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SalonEmployeesRepo salonEmployeesRepo;

    @Override
    @Transactional
    public ApiResponse<RigesterResponse> Rigester (RigesterRequest request) {

        if (request.getPhoneNumber() == null ||
                (!request.getPhoneNumber().matches("^\\+9627\\d{8}$")
                        && !request.getPhoneNumber().matches("^07\\d{8}$"))) {
            return ApiResponse.error("Phone number must be like +9627XXXXXXXX or 07XXXXXXXX");
        }

        if (userRepo.existsByEmail(request.getEmail())) {
            return ApiResponse.error("Email already exists");
        }

        if (userRepo.existsByUsername(request.getUserName())) {
            return ApiResponse.error("UserName is already used");
        }

        if (userRepo.existsByPhoneNumber(request.getPhoneNumber())) {
            return ApiResponse.error("Phone number is already used");
        }

        User user = new User();
        user.setEmail(request.getEmail().trim());
        user.setUsername(request.getUserName().trim());
        user.setPhoneNumber(request.getPhoneNumber().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        User savedUser = userRepo.save(user);

        linkInvitedEmployeeIfExists(savedUser);

        RigesterResponse response = new RigesterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getUsername(),
                savedUser.getRole()
        );

        return ApiResponse.success("Registered successfully", response);
    }
    @Override
    public ApiResponse<LoginResponse> Login(LoginRequest loginRequest) {

        String email = loginRequest.getEmail().trim();
        String rawPassword = loginRequest.getPassword();

        Optional<User> userop = userRepo.findByEmail(email);

        if (userop.isEmpty()) {
            return ApiResponse.error("User With This Email Not Exist");
        }

        User user = userop.get();

        boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
        System.out.println("Login password = [" + loginRequest.getPassword() + "]");
        System.out.println("DB password = [" + user.getPassword() + "]");
        System.out.println("Matches = " + passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()));
        if (!matches) {
            return ApiResponse.error("Incorrect Password");
        }

        String token = jwtService.generateToken(user.getEmail());

        LoginResponse response = new LoginResponse(
                token,
                "Bearer",
                user.getEmail(),
                user.getUsername(),
                user.getRole(),
                user.getId()
        );


        return ApiResponse.success("Login Successfully", response);
    }


    private void linkInvitedEmployeeIfExists(User savedUser) {

        Salonemps invitation = salonEmployeesRepo
                .findFirstByEmailIgnoreCaseAndStatus(savedUser.getEmail(), EmployeeStatus.INVITED)
                .orElse(null);

        if (invitation == null) {
            invitation = salonEmployeesRepo
                    .findFirstByPhoneNumberAndStatus(savedUser.getPhoneNumber(), EmployeeStatus.INVITED)
                    .orElse(null);
        }

        if (invitation == null) {
            return;
        }

        invitation.setEmployee(savedUser);
        invitation.setStatus(EmployeeStatus.ACTIVE);
        invitation.setJoinedAt(LocalDateTime.now());
        salonEmployeesRepo.save(invitation);

        savedUser.setRole(Role.EMPLOYEE);
        savedUser.setSalon(invitation.getSalon());
        userRepo.save(savedUser);
    }
}
