package com.example.gym_crm.authentification;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
public class AuthData {
    private String username;
    private String password;

    public AuthData(String username, String password) {
        this.username = Objects.requireNonNull(username, "username is required");
        this.password = Objects.requireNonNull(password, "password is required");
    }
}
