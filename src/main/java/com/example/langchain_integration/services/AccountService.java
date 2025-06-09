package com.example.langchain_integration.services;

import com.example.langchain_integration.dto.LoginRequest;
import com.example.langchain_integration.dto.SignupRequest;
import com.example.langchain_integration.model.User;
import com.example.langchain_integration.repository.UserRepository;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.Claims;

import java.util.Set;

@ApplicationScoped
public class AccountService {
    @Inject
    UserRepository userRepository;

    @Inject
    PasswordService passwordService;

    public User signupUser(SignupRequest request, String creator) {
        return userRepository.createUser(
                request.username, request.password,
                request.role, request.email, creator);
    }

    public Response loginUser(LoginRequest login) {
        User user = User.findByEmail(login.email);
        if (user == null || !passwordService.verifyPassword(login.password, user.password)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }

        String token = Jwt.issuer("https://translator-app")
                .upn(user.username)
                .claim(Claims.email, user.email)
                .groups(Set.of(user.role)).sign();

        return Response.ok("{\"token\":\"" + token + "\"}").build();
    }
}