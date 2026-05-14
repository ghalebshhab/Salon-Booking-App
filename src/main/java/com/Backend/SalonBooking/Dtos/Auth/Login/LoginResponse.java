package com.Backend.SalonBooking.Dtos.Auth.Login;

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
    private String password;
    private String userName;
    private String role;
}
