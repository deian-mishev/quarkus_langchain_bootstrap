package com.example.langchain_integration.services;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PasswordService {

    public String hashPassword(String plainPassword) {
        return BcryptUtil.bcryptHash(plainPassword);
    }

    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BcryptUtil.matches(plainPassword, hashedPassword);
    }
}
