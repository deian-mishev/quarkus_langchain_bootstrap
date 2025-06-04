package com.example.langchain_integration.resources;

import com.example.langchain_integration.dto.LoginRequest;
import com.example.langchain_integration.dto.SignupRequest;
import com.example.langchain_integration.model.User;
import com.example.langchain_integration.services.AccountService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.smallrye.jwt.build.Jwt;

import java.util.Set;

@Path("/users")
public class UserResource {

    @Inject
    AccountService accountService;

    @POST
    @RolesAllowed("admin")
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response signup(@Valid SignupRequest request) {
        User user = accountService.signupUser(request.username, request.password, request.role);
        return Response.ok("User created with id: " + user.id).build();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Valid LoginRequest login) {
        User user = accountService.findByUsername(login.username);
        if (user == null || !accountService.verifyPassword(login.password, user.password)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }

        String token = Jwt.issuer("https://translator-app").upn(user.username).groups(Set.of(user.role)).sign();

        return Response.ok("{\"token\":\"" + token + "\"}").build();
    }
}