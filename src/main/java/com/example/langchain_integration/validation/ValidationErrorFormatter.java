package com.example.langchain_integration.validation;

import com.example.langchain_integration.dto.TranslationRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintViolation;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class ValidationErrorFormatter {

    public String formatViolation(Set<ConstraintViolation<TranslationRequest>> violationSet) {
        String errors = violationSet.stream()
                .sorted(Comparator.comparing((ConstraintViolation<?> cv) ->
                        !cv.getMessage().toLowerCase().contains("required")))
                .map(cv -> "- " + propertyPathToString(cv) + ": " + cv.getMessage())
                .collect(Collectors.joining("\n"));

        return "Validation failed:\n" + errors;
    }

    private String propertyPathToString(ConstraintViolation<?> violation) {
        return violation.getPropertyPath().toString();
    }
    
    public String formatViolation(String ex) {
        return "Validation failed: " + ex;
    }
}