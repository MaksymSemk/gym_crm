package com.example.trainerworkloadservice.repository;

import com.example.trainerworkloadservice.workload.model.TrainerWorkload;
import com.example.trainerworkloadservice.workload.repository.TrainerWorkloadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TrainerWorkloadRepositoryTest {

    private TrainerWorkloadRepository repository;

    @BeforeEach
    void setUp() {
        repository = new TrainerWorkloadRepository();
    }

    @Test
    @DisplayName("save and findByUsername stores and retrieves correctly")
    void saveAndFind() {
        TrainerWorkload workload = new TrainerWorkload("alex.turner", "Alex", "Turner", true, new ArrayList<>());
        repository.save(workload);

        Optional<TrainerWorkload> result = repository.findByUsername("alex.turner");

        assertThat(result).isPresent();
        assertThat(result.get().getTrainerUsername()).isEqualTo("alex.turner");
    }

    @Test
    @DisplayName("findByUsername returns empty when not found")
    void findByUsername_NotFound() {
        Optional<TrainerWorkload> result = repository.findByUsername("missing.user");
        assertThat(result).isEmpty();
    }
}