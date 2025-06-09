package com.example.langchain_integration.model;

import com.example.langchain_integration.validation.UniqueEmail;
import com.example.langchain_integration.validation.ValidUserRole;
import jakarta.validation.constraints.Null;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "users")
public class User extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Email
    @NotBlank
    @UniqueEmail
    @Column(nullable = false, unique = true)
    public String email;

    @NotBlank
    @Column(nullable = false, unique = true)
    public String username;

    @NotBlank
    @Column(nullable = false)
    public String password;

    @ValidUserRole
    @NotBlank
    public String role;

    @Column
    public String creator;

    public static User findByUsername(String username) {
        return find("username", username).firstResult();
    }
    public static User findByEmail(String email) {
        return find("email", email).firstResult();
    }
}
