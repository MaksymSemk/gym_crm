package com.example.gym_crm.cucumber;

import com.example.gym_crm.common.rate_limiting.LoginRateLimitFilter;
import io.cucumber.java.After;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;

public class ScenarioCleanUpHook {

    private final JdbcTemplate jdbcTemplate;
    private final LoginRateLimitFilter loginRateLimitFilter;

    public ScenarioCleanUpHook(JdbcTemplate jdbcTemplate, LoginRateLimitFilter loginRateLimitFilter) {
        this.jdbcTemplate = jdbcTemplate;
        this.loginRateLimitFilter = loginRateLimitFilter;
    }

    @After(order = 1000)
    public void cleanUp() {
        jdbcTemplate.execute("DELETE FROM trainings");
        jdbcTemplate.execute("DELETE FROM trainer_trainee");
        jdbcTemplate.execute("DELETE FROM trainees");
        jdbcTemplate.execute("DELETE FROM trainers");
        jdbcTemplate.execute("DELETE FROM users");

        // Clear in-memory lockout map so subsequent scenarios start fresh
        loginRateLimitFilter.reset();
        SecurityContextHolder.clearContext();
    }
}