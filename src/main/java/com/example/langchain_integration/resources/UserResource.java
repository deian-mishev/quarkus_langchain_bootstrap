package com.example.langchain_integration.resources;

import com.example.langchain_integration.dto.LoginRequest;
import com.example.langchain_integration.dto.SignupRequest;
import com.example.langchain_integration.model.User;
import com.example.langchain_integration.services.AccountService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;

@Path("/users")
@RequestScoped
public class UserResource {

    @Inject
    AccountService accountService;

    @Inject
    @Claim(standard = Claims.email)
    String email;

    @POST
    @Path("/signup")
    @RolesAllowed("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response signup(@Valid SignupRequest request) {
        User user = accountService.signupUser(request, email);
        return Response.ok("User created with id: " + user.id).build();
    }

    @POST
    @Path("/login")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Valid LoginRequest login) {
        return accountService.loginUser(login);
    }
}