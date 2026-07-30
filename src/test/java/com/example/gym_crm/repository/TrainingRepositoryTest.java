package com.example.gym_crm.repository;

import com.example.gym_crm.common.user.User;
import com.example.gym_crm.trainee.Trainee;
import com.example.gym_crm.trainee.repository.TraineeRepository;
import com.example.gym_crm.trainer.Trainer;
import com.example.gym_crm.trainer.repository.TrainerRepository;
import com.example.gym_crm.training.Training;
import com.example.gym_crm.training.repository.TrainingRepository;
import com.example.gym_crm.training_type.TrainingType;
import com.example.gym_crm.training_type.repository.TrainingTypeRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class TrainingRepositoryTest extends BaseRepositoryTest {

    @Autowired private TrainingRepository trainingRepository;
    @Autowired private TraineeRepository traineeRepository;
    @Autowired private TrainerRepository trainerRepository;
    @Autowired private TrainingTypeRepository trainingTypeRepository;

    @BeforeEach
    void seedData() {
        TrainingType typeYoga = new TrainingType();
        typeYoga.setName("Yoga");
        trainingTypeRepository.save(typeYoga);

        User u1 = User.builder().firstName("John").lastName("Doe").username("John.Doe").password("p1").isActive(true).build();
        Trainee traineeJohn = Trainee.builder().user(u1).build();
        traineeRepository.save(traineeJohn);

        User u2 = User.builder().firstName("Emma").lastName("Watson").username("Emma.Watson").password("p2").isActive(true).build();
        Trainer trainerEmma = Trainer.builder().specialization(typeYoga).user(u2).build();
        trainerRepository.save(trainerEmma);

        Training session1 = Training.builder()
                .trainee(traineeJohn).trainer(trainerEmma).trainingType(typeYoga)
                .trainingName("Morning Flow").trainingDate(LocalDate.of(2026, 7, 10)).trainingDuration(60).build();

        Training session2 = Training.builder()
                .trainee(traineeJohn).trainer(trainerEmma).trainingType(typeYoga)
                .trainingName("Evening Rest").trainingDate(LocalDate.of(2026, 7, 20)).trainingDuration(90).build();

        trainingRepository.save(session1);
        trainingRepository.save(session2);
    }

    @Test
    @DisplayName("findTraineeTrainingsByCriteria: All filters match successfully")
    void findTraineeTrainings_AllFiltersMatch() {
        List<Training> results = trainingRepository.findTraineeTrainingsByCriteria(
                "John.Doe", LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 15), "Emma.Watson", "Yoga"
        );
        assertEquals(1, results.size());
        assertEquals("Morning Flow", results.get(0).getTrainingName());
    }

    @Test
    @DisplayName("findTraineeTrainingsByCriteria: Dynamic clean handle when optional arguments are null or empty strings")
    void findTraineeTrainings_OptionalFiltersNullOrBlank() {
        List<Training> results = trainingRepository.findTraineeTrainingsByCriteria(
                "John.Doe", null, null, " ", ""
        );
        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("findTraineeTrainingsByCriteria: Return empty list if date range contains no matching elements")
    void findTraineeTrainings_OutofDateRange() {
        List<Training> results = trainingRepository.findTraineeTrainingsByCriteria(
                "John.Doe", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 30), null, null
        );
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("findTrainerTrainingsByCriteria: All filters match successfully")
    void findTrainerTrainings_AllFiltersMatch() {
        List<Training> results = trainingRepository.findTrainerTrainingsByCriteria(
                "Emma.Watson", LocalDate.of(2026, 7, 15), LocalDate.of(2026, 7, 25), "John.Doe"
        );
        assertEquals(1, results.size());
        assertEquals("Evening Rest", results.get(0).getTrainingName());
    }

    @Test
    @DisplayName("findTrainerTrainingsByCriteria: Gracefully handle absent filter variables")
    void findTrainerTrainings_MissingOptionalFilters() {
        List<Training> results = trainingRepository.findTrainerTrainingsByCriteria(
                "Emma.Watson", null, null, null
        );
        assertEquals(2, results.size());
    }
}