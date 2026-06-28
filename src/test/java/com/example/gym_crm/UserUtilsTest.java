package com.example.gym_crm;

import com.example.gym_crm.common.user.UserUtils;
import com.example.gym_crm.trainee.Trainee;
import com.example.gym_crm.trainee.TraineeRepository;
import com.example.gym_crm.trainer.Trainer;
import com.example.gym_crm.trainer.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUtilsTest {

    private UserUtils userUtils;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @BeforeEach
    void setUp() {
        userUtils = new UserUtils();
        userUtils.setTrainerRepository(trainerRepository);
        userUtils.setTraineeRepository(traineeRepository);
    }

    @Nested
    @DisplayName("Username Generation Tests")
    class UsernameGenerationTests {

        @Test
        @DisplayName("Should generate a clean dot-separated username when no naming collision exists")
        void createUsername_NoCollision_Success() {
            when(trainerRepository.findAll()).thenReturn(Collections.emptyList());
            when(traineeRepository.findAll()).thenReturn(Collections.emptyList());

            String username = userUtils.createUsername("John", "Smith");

            assertEquals("John.Smith", username);
            verify(trainerRepository, times(1)).findAll();
            verify(traineeRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should trim whitespace variations from both name components during generation")
        void createUsername_TrimsInputNames() {
            when(trainerRepository.findAll()).thenReturn(Collections.emptyList());
            when(traineeRepository.findAll()).thenReturn(Collections.emptyList());

            String username = userUtils.createUsername("  John  ", "  Smith  ");

            assertEquals("John.Smith", username);
        }

        @Test
        @DisplayName("Should append serial suffix increment when base username collides with an existing Trainer")
        void createUsername_CollisionWithTrainer_AppendsSuffix() {
            Trainer existingTrainer = new Trainer();
            existingTrainer.setUsername("John.Smith");

            when(trainerRepository.findAll()).thenReturn(List.of(existingTrainer));
            when(traineeRepository.findAll()).thenReturn(Collections.emptyList());

            String username = userUtils.createUsername("John", "Smith");

            assertEquals("John.Smith1", username);
        }

        @Test
        @DisplayName("Should append serial suffix increment when base username collides with an existing Trainee")
        void createUsername_CollisionWithTrainee_AppendsSuffix() {
            Trainee existingTrainee = new Trainee();
            existingTrainee.setUsername("John.Smith");

            when(trainerRepository.findAll()).thenReturn(Collections.emptyList());
            when(traineeRepository.findAll()).thenReturn(List.of(existingTrainee));

            String username = userUtils.createUsername("John", "Smith");

            assertEquals("John.Smith1", username);
        }

        @Test
        @DisplayName("Should increment suffix recursively until a completely unique variant is discovered")
        void createUsername_MultipleCollisions_IncrementsUntilUnique() {
            Trainer trainer1 = new Trainer();
            trainer1.setUsername("John.Smith");

            Trainee trainee1 = new Trainee();
            trainee1.setUsername("John.Smith1");

            Trainer trainer2 = new Trainer();
            trainer2.setUsername("John.Smith2");

            when(trainerRepository.findAll()).thenReturn(List.of(trainer1, trainer2));
            when(traineeRepository.findAll()).thenReturn(List.of(trainee1));

            String username = userUtils.createUsername("John", "Smith");

            assertEquals("John.Smith3", username);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException immediately if firstName parameter is null")
        void createUsername_NullFirstName_ThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> userUtils.createUsername(null, "Smith"));
            verifyNoInteractions(trainerRepository, traineeRepository);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException immediately if lastName parameter is null")
        void createUsername_NullLastName_ThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> userUtils.createUsername("John", null));
            verifyNoInteractions(trainerRepository, traineeRepository);
        }
    }

    @Nested
    @DisplayName("Password Generation Tests")
    class PasswordGenerationTests {

        @Test
        @DisplayName("Should satisfy explicit structural guidelines and character boundaries")
        void generatePassword_ValidatesStructure() {
            String password = userUtils.generatePassword();

            assertNotNull(password);
            assertEquals(10, password.length(), "Password must be exactly 10 characters long");

            String allowedPool = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{}|;:,.<>?";
            for (char ch : password.toCharArray()) {
                assertTrue(allowedPool.indexOf(ch) >= 0,
                        String.format("Generated password contains illegal characters: %c", ch));
            }
        }

        @Test
        @DisplayName("Should produce highly random non-identical outputs on successive method executions")
        void generatePassword_ProducesDistinctOutputs() {
            String pass1 = userUtils.generatePassword();
            String pass2 = userUtils.generatePassword();
            String pass3 = userUtils.generatePassword();

            assertNotEquals(pass1, pass2);
            assertNotEquals(pass2, pass3);
        }
    }
}