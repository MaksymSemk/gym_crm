package com.example.gym_crm.authentification;

import java.util.Objects;

public record AuthData(
        String username,
        String password
) {
    public AuthData {
        Objects.requireNonNull(username, "username is required");
        Objects.requireNonNull(password, "password is required");
    }
}
