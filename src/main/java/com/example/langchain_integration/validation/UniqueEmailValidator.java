package com.example.langchain_integration.validation;

import com.example.langchain_integration.model.User;
import com.example.langchain_integration.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@ApplicationScoped
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Inject
    UserRepository userRepository;
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) return true;
        return !userRepository.emailExists(email);
    }
}