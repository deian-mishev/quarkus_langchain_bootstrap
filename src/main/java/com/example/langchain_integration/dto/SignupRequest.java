package com.example.langchain_integration.dto;

import com.example.langchain_integration.validation.ValidUserRole;
import jakarta.validation.constraints.NotBlank;

public class SignupRequest {

    @NotBlank
    public String username;

    @NotBlank
    public String password;

    @NotBlank
    @ValidUserRole
    public String role;
}