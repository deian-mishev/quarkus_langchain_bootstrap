package com.example.langchain_integration.dto;

import jakarta.validation.constraints.NotBlank;

public class TranslationRequest {

    @NotBlank(message = "Message must not be blank")
    private String message;

    @NotBlank(message = "Required translation language must be provided.")
    private String language;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}