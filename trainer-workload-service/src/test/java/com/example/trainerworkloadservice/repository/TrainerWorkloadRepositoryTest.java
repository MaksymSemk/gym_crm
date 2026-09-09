package com.example.trainerworkloadservice.repository;

import com.example.trainerworkloadservice.workload.model.MonthSummary;
import com.example.trainerworkloadservice.workload.model.TrainerWorkload;
import com.example.trainerworkloadservice.workload.model.YearSummary;
import com.example.trainerworkloadservice.workload.repository.TrainerWorkloadRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class TrainerWorkloadRepositoryTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void configureMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private TrainerWorkloadRepository repository;

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("save and findByTrainerUsername stores and retrieves correctly")
    void saveAndFind() {
        TrainerWorkload workload = new TrainerWorkload(
                "alex.turner",
                "Alex",
                "Turner",
                true,
                new ArrayList<>(List.of(
                        new YearSummary(2026, new ArrayList<>(List.of(new MonthSummary(8, 90))))
                ))
        );

        repository.save(workload);

        Optional<TrainerWorkload> result = repository.findByTrainerUsername("alex.turner");
        assertThat(result).isPresent();
        assertThat(result.get().getTrainerUsername()).isEqualTo("alex.turner");
        assertThat(result.get().getYears().get(0).getMonths().get(0).getTrainingSummaryDuration()).isEqualTo(90);
    }

    @Test
    @DisplayName("findByTrainerUsername returns empty when document does not exist")
    void findByTrainerUsername_NotFound() {
        Optional<TrainerWorkload> result = repository.findByTrainerUsername("missing.user");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByTrainerFirstNameAndTrainerLastName finds records matching compound index")
    void findByTrainerFirstNameAndTrainerLastName_MatchesIndexedFields() {
        TrainerWorkload workload1 = new TrainerWorkload("coach.bob", "Bob", "Smith", true, new ArrayList<>());
        TrainerWorkload workload2 = new TrainerWorkload("trainer.alice", "Alice", "Smith", true, new ArrayList<>());
        repository.saveAll(List.of(workload1, workload2));

        List<TrainerWorkload> results = repository.findByTrainerFirstNameAndTrainerLastName("Bob", "Smith");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTrainerUsername()).isEqualTo("coach.bob");
    }
}