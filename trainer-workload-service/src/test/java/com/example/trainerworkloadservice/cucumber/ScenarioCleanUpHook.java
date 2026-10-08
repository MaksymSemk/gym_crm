package com.example.trainerworkloadservice.cucumber;

import com.example.trainerworkloadservice.workload.repository.TrainerWorkloadRepository;
import io.cucumber.java.After;
import org.springframework.security.core.context.SecurityContextHolder;

public class ScenarioCleanUpHook {

    private final TrainerWorkloadRepository repository;

    public ScenarioCleanUpHook(TrainerWorkloadRepository repository) {
        this.repository = repository;
    }

    @After(order = 1000)
    public void cleanUp() {
        repository.deleteAll();
        SecurityContextHolder.clearContext();
    }
}