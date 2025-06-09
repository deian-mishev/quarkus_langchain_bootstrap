package com.example.langchain_integration.repository;

import com.example.langchain_integration.model.User;
import com.example.langchain_integration.services.PasswordService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    @Inject
    PasswordService passwordService;

    @Transactional
    public User createUser(String username, String password,
                           String role, String email,
                           String creator) {
        User existingUser = User.findByUsername(username);
        if (existingUser != null) {
            throw new IllegalArgumentException("Username already exists!");
        }

        String encryptedPassword = passwordService.hashPassword(password);

        User user = new User();
        user.username = username;
        user.password = encryptedPassword;
        user.role = role;
        user.email = email;
        user.creator = creator;
        user.persist();

        return user;
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public boolean emailExists(String email) {
        return User.find("email", email).firstResult() != null;
    }
}