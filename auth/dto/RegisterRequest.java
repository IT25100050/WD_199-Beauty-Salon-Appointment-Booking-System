package com.beautysalon.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Role is required")
    private String role; // ADMIN, CUSTOMER, STAFF

    // Customer specific fields
    private String phoneNumber;
    private String address;

    // Staff specific fields
    private String specialization;
    private String schedule;

    // Admin specific fields
    private boolean isSuperAdmin;
}
