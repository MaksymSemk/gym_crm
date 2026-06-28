package com.example.gym_crm;

import com.example.gym_crm.training.Training;
import com.example.gym_crm.training.TrainingId;
import com.example.gym_crm.training.TrainingIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
class TrainingIdGeneratorTest {

    private TrainingIdGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new TrainingIdGenerator();
    }

    @Nested
    @DisplayName("Entity Type Validation Tests")
    class TypeValidationTests {

        @Test
        @DisplayName("Should throw IllegalArgumentException when entity is not an instance of Training")
        void generateNewId_InvalidEntityType_ThrowsException() {
            Object invalidEntity = "I am a String, not a Training object";

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    generator.generateNewId(invalidEntity)
            );

            assertEquals("Entity must be an instance of Training", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when entity is null")
        void generateNewId_NullEntity_ThrowsException() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    generator.generateNewId(null)
            );

            assertEquals("Entity must be an instance of Training", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Composite Key Parameter Validation Tests")
    class CompositeKeyValidationTests {

        @Test
        @DisplayName("Should throw NullPointerException when composite TrainingId object itself is null")
        void generateNewId_NullTrainingId_ThrowsException() {
            Training training = new Training();
            training.setId(null);

            assertThrows(NullPointerException.class, () ->
                    generator.generateNewId(training)
            );
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when traineeId parameter inside TrainingId is null")
        void generateNewId_NullTraineeId_ThrowsException() {
            // Fix: Mock locally within the test to avoid outer-instance lifecycle initialization failure
            TrainingId localMockId = mock(TrainingId.class);
            Training training = new Training();
            training.setId(localMockId);

            when(localMockId.traineeId()).thenReturn(null);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    generator.generateNewId(training)
            );

            assertTrue(exception.getMessage().contains("traineeId, trainerId, and trainingDate must not be null"));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when trainerId parameter inside TrainingId is null")
        void generateNewId_NullTrainerId_ThrowsException() {
            TrainingId localMockId = mock(TrainingId.class);
            Training training = new Training();
            training.setId(localMockId);

            when(localMockId.traineeId()).thenReturn(UUID.randomUUID());
            when(localMockId.trainerId()).thenReturn(null);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    generator.generateNewId(training)
            );

            assertTrue(exception.getMessage().contains("traineeId, trainerId, and trainingDate must not be null"));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when trainingDate parameter inside TrainingId is null")
        void generateNewId_NullTrainingDate_ThrowsException() {
            TrainingId localMockId = mock(TrainingId.class);
            Training training = new Training();
            training.setId(localMockId);

            when(localMockId.traineeId()).thenReturn(UUID.randomUUID());
            when(localMockId.trainerId()).thenReturn(UUID.randomUUID());
            when(localMockId.trainingDate()).thenReturn(null);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    generator.generateNewId(training)
            );

            assertTrue(exception.getMessage().contains("traineeId, trainerId, and trainingDate must not be null"));
        }
    }

    @Nested
    @DisplayName("Successful Key Extraction Tests")
    class SuccessTests {

        @Test
        @DisplayName("Should cleanly return the valid composite key from Training object")
        void generateNewId_ValidTraining_ReturnsTrainingId() {
            TrainingId realId = new TrainingId(UUID.randomUUID(), UUID.randomUUID(), LocalDate.of(2026, 6, 1));
            Training training = new Training();
            training.setId(realId);

            TrainingId resultId = generator.generateNewId(training);

            assertNotNull(resultId);
            assertEquals(realId, resultId);
        }
    }
}