package com.example.gym_crm.common.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityHealthIndicator implements HealthIndicator {

    private final PasswordEncoder passwordEncoder;

    @Override
    public Health health() {
        try {
            String raw = "health_check_password_encoding";
            String encoded = passwordEncoder.encode(raw);
            boolean matches = passwordEncoder.matches(raw, encoded);

            if (matches) {
                return Health.up()
                        .withDetail("passwordEncoder", passwordEncoder.getClass().getSimpleName())
                        .withDetail("status", "PasswordEncoder functioning normally")
                        .build();
            } else {
                return Health.down()
                        .withDetail("error", "PasswordEncoder verification failed")
                        .build();
            }
        } catch (Exception ex) {
            return Health.down(ex).build();
        }
    }
}