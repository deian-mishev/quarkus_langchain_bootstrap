package com.example.langchain_integration.services;

import com.example.langchain_integration.model.User;
import com.example.langchain_integration.validation.ValidUserRole;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;

@ApplicationScoped
public class AccountService {

    @Transactional
    public User signupUser(@NotBlank String username, @NotBlank String password, @ValidUserRole @NotBlank String role) {
        User existingUser = User.findByUsername(username);
        if (existingUser != null) {
            throw new IllegalArgumentException("Username already exists!");
        }

        String encryptedPassword = BcryptUtil.bcryptHash(password);

        User user = new User();
        user.username = username;
        user.password = encryptedPassword;
        user.role = role;
        user.persist();

        return user;
    }

    public boolean verifyPassword(String rawPassword, String encryptedPassword) {
        return BcryptUtil.matches(rawPassword, encryptedPassword);
    }

    public User findByUsername(@NotBlank String username) {
        return User.findByUsername(username);
    }
}