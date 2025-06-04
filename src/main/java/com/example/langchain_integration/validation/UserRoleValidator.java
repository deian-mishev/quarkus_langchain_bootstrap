package com.example.langchain_integration.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class UserRoleValidator implements ConstraintValidator<ValidUserRole, String> {

    private static final Set<String> VALID_ROLES = Set.of("user", "admin");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && VALID_ROLES.contains(value.toLowerCase());
    }
}