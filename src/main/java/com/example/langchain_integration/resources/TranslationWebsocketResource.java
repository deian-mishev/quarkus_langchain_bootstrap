package com.example.langchain_integration.resources;

import com.example.langchain_integration.dto.TranslationRequest;
import com.example.langchain_integration.exceptions.PromptInjectionGuard;
import com.example.langchain_integration.exceptions.ValidationErrorFormatter;
import dev.langchain4j.data.message.UserMessage;
import com.example.langchain_integration.services.TranslationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.inject.Inject;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;

import java.util.Set;

@WebSocket(path = "/translate")
public class TranslationWebsocketResource {

    @Inject
    ValidationErrorFormatter validationErrorFormatter;

    @Inject
    TranslationService translationService;

    @Inject
    Validator validator;

    @Inject
    PromptInjectionGuard promptInjectionGuard;

    @Inject
    ObjectMapper objectMapper;

    @OnOpen
    public String onOpen() {
        return "Welcome to your language translation service! How can I help you today?";
    }

    @OnTextMessage
    public Multi<String> onTextMessage(String userQuery) {
        return Multi.createFrom().emitter(emitter -> {
            Infrastructure.getDefaultWorkerPool().execute(() -> {
                try {
                    TranslationRequest req = objectMapper.readValue(userQuery, TranslationRequest.class);
                    Set<ConstraintViolation<TranslationRequest>> violations = validator.validate(req);
                    if (!violations.isEmpty()) {
                        emitter.emit(validationErrorFormatter.formatViolation(violations));
                        emitter.complete();
                        return;
                    }

                    if (!promptInjectionGuard.validate(UserMessage.userMessage(req.getMessage())).isSuccess()) {
                        emitter.emit(validationErrorFormatter.formatViolation("Prompt injection detected"));
                        emitter.complete();
                        return;
                    }

                    translationService.translate(req.getMessage(), req.getLanguage())
                            .subscribe().with(
                                    emitter::emit,
                                    failure -> {
                                        emitter.emit(validationErrorFormatter.formatViolation("Error: " + failure.getMessage()));
                                        emitter.complete();
                                    },
                                    emitter::complete);

                } catch (Exception e) {
                    emitter.emit(validationErrorFormatter.formatViolation("Error: " + e.getMessage()));
                    emitter.complete();
                }
            });
        });
    }
}
