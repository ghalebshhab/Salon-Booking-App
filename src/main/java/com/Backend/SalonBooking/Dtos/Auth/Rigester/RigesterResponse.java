package com.Backend.SalonBooking.Dtos.Auth.Rigester;

import com.Backend.SalonBooking.Entities.Users.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RigesterResponse {

    private Long id;
    private String email;
    private String username;
    private Role role;

}
