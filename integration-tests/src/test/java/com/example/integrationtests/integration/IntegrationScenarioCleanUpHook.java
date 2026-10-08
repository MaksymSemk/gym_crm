package com.example.integrationtests.integration;

import com.example.trainerworkloadservice.workload.repository.TrainerWorkloadRepository;
import io.cucumber.java.After;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;

public class IntegrationScenarioCleanUpHook {

    private final JdbcTemplate jdbcTemplate;
    private final TrainerWorkloadRepository mongoRepository;

    public IntegrationScenarioCleanUpHook(JdbcTemplate jdbcTemplate, TrainerWorkloadRepository mongoRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.mongoRepository = mongoRepository;
    }

    @After(order = 1000)
    public void cleanUp() {
        jdbcTemplate.execute("DELETE FROM trainings");
        jdbcTemplate.execute("DELETE FROM trainer_trainee");
        jdbcTemplate.execute("DELETE FROM trainees");
        jdbcTemplate.execute("DELETE FROM trainers");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM training_types");

        mongoRepository.deleteAll();

        SecurityContextHolder.clearContext();
    }
}
