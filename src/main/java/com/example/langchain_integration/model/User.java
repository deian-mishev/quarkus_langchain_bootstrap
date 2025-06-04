package com.example.langchain_integration.model;

import com.example.langchain_integration.validation.ValidUserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "users")
public class User extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    public String username;

    @NotBlank
    @Column(nullable = false)
    public String password;

    @ValidUserRole
    @NotBlank
    @Column(nullable = false)
    public String role;

    public static User findByUsername(String username) {
        return find("username", username).firstResult();
    }
}
