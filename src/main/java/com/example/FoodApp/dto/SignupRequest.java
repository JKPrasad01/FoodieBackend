package com.example.FoodApp.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {

    @NotBlank(message = "Username  is required")
    @Size(min = 3,max = 20,message = "User name is must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "email is required")
    @Email(message = "Please provider valid email address")
    @Size(max = 50,message = "Email must be exceed 50 characters")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 6,max = 30,message = "password must be between 6 and 40 characters")
    private String password;
}
