package com.example.langchain_integration.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank
    @Email
    public String email;
    @NotBlank
    public String password;
}