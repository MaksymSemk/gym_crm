package com.example.gym_crm.common.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUtilsTest {

    private UserUtils userUtils;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userUtils = new UserUtils();
        userUtils.setUserRepository(userRepository);
    }

    @Nested
    @DisplayName("Username Generation Tests")
    class UsernameGenerationTests {

        @Test
        @DisplayName("Should generate a clean dot-separated username when no naming collision exists")
        void createUsername_NoCollision_Success() {
            when(userRepository.findByUsername("John.Smith")).thenReturn(Optional.empty());

            String username = userUtils.createUsername("John", "Smith");

            assertEquals("John.Smith", username);
            verify(userRepository, times(1)).findByUsername("John.Smith");
        }

        @Test
        @DisplayName("Should trim whitespace variations from both name components during generation")
        void createUsername_TrimsInputNames() {
            when(userRepository.findByUsername("John.Smith")).thenReturn(Optional.empty());

            String username = userUtils.createUsername("  John  ", "  Smith  ");

            assertEquals("John.Smith", username);
        }

        @Test
        @DisplayName("Should append serial suffix increment when base username collides")
        void createUsername_SingleCollision_AppendsSuffix() {
            when(userRepository.findByUsername("John.Smith")).thenReturn(Optional.of(new User()));
            when(userRepository.findByUsername("John.Smith1")).thenReturn(Optional.empty());

            String username = userUtils.createUsername("John", "Smith");

            assertEquals("John.Smith1", username);
            verify(userRepository).findByUsername("John.Smith");
            verify(userRepository).findByUsername("John.Smith1");
        }

        @Test
        @DisplayName("Should increment suffix recursively until a completely unique variant is discovered")
        void createUsername_MultipleCollisions_IncrementsUntilUnique() {
            when(userRepository.findByUsername("John.Smith")).thenReturn(Optional.of(new User()));
            when(userRepository.findByUsername("John.Smith1")).thenReturn(Optional.of(new User()));
            when(userRepository.findByUsername("John.Smith2")).thenReturn(Optional.of(new User()));
            when(userRepository.findByUsername("John.Smith3")).thenReturn(Optional.empty());

            String username = userUtils.createUsername("John", "Smith");

            assertEquals("John.Smith3", username);
            verify(userRepository, times(4)).findByUsername(anyString());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException immediately if firstName parameter is null")
        void createUsername_NullFirstName_ThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> userUtils.createUsername(null, "Smith"));
            verifyNoInteractions(userRepository);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException immediately if lastName parameter is null")
        void createUsername_NullLastName_ThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> userUtils.createUsername("John", null));
            verifyNoInteractions(userRepository);
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
        @DisplayName("Should produce non-identical outputs on successive method executions")
        void generatePassword_ProducesDistinctOutputs() {
            String pass1 = userUtils.generatePassword();
            String pass2 = userUtils.generatePassword();

            assertNotEquals(pass1, pass2);
        }
    }
}