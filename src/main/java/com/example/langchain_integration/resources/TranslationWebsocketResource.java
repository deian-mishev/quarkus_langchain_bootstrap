package com.example.langchain_integration.resources;

import com.example.langchain_integration.dto.TranslationRequest;
import com.example.langchain_integration.validation.PromptInjectionGuard;
import com.example.langchain_integration.validation.ValidationErrorFormatter;
import dev.langchain4j.data.message.UserMessage;
import com.example.langchain_integration.services.TranslationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.subscription.MultiEmitter;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;

import java.util.Set;

@WebSocket(path = "/translate")
@RolesAllowed("user")
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
                    if (handleViolations(emitter, violations)) return;

                    if (!promptInjectionGuard.validate(UserMessage.userMessage(req.getMessage())).isSuccess()) {
                        emitError(emitter, "Prompt injection detected");
                        return;
                    }

                    translationService.translate(req.getMessage(), req.getLanguage())
                            .subscribe().with(
                                    emitter::emit,
                                    failure -> emitError(emitter, "Error: " + failure.getMessage()),
                                    emitter::complete);

                } catch (Exception e) {
                    emitError(emitter, "Error: " + e.getMessage());
                }
            });
        });
    }

    private void emitError(MultiEmitter<? super String> emitter, String message) {
        if (emitter != null && message != null) {
            emitter.emit(validationErrorFormatter.formatViolation(message));
            emitter.complete();
        }
    }

    private boolean handleViolations(MultiEmitter<? super String> emitter,
                                     Set<ConstraintViolation<TranslationRequest>> violations) {
        if (violations != null && !violations.isEmpty()) {
            emitter.emit(validationErrorFormatter.formatViolation(violations));
            emitter.complete();
            return true;
        }
        return false;
    }
}
