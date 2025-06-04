package com.example.langchain_integration.validation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@ApplicationScoped
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Inject
    ValidationErrorFormatter validationErrorFormatter;

    @Override
    public Response toResponse(Exception exception) {
        String message;
        if (exception instanceof IllegalArgumentException) {
            message = validationErrorFormatter.formatViolation(exception.getMessage());
        } else {
            // For other exceptions, you can customize or just send the message:
            message = "Error: " + exception.getMessage();
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(message).type(MediaType.TEXT_PLAIN).build();
    }
}
