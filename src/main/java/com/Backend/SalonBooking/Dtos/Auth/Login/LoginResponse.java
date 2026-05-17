package com.Backend.SalonBooking.Dtos.Auth.Login;

import com.Backend.SalonBooking.Entities.Users.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private String token;
    private String type="Bearer";
    private String email;
    private String userName;
    private Role role;
    private Long Id;
}
