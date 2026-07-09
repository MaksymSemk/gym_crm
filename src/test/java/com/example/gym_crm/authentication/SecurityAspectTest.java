package com.example.gym_crm.authentication;

import com.example.gym_crm.common.user.User;
import com.example.gym_crm.common.user.UserRepository;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityAspectTest {

    private SecurityAspect securityAspect;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private RequiresAuth requiresAuth;

    @BeforeEach
    void setUp() {
        securityAspect = new SecurityAspect(userRepository);
    }

    @Nested
    @DisplayName("Authentication Success Tests")
    class SuccessTests {

        @Test
        @DisplayName("Should permit method execution when valid AuthData matches database credentials")
        void authenticate_ValidCredentials_PassesSilently() {
            AuthData validAuth = new AuthData();
            validAuth.setUsername("trainer.test");
            validAuth.setPassword("correct_pass");

            User matchedUser = User.builder()
                    .id(UUID.randomUUID())
                    .firstName("Test")
                    .lastName("Trainer")
                    .username("trainer.test")
                    .password("correct_pass")
                    .isActive(true)
                    .build();

            when(joinPoint.getArgs()).thenReturn(new Object[]{validAuth});
            when(userRepository.findByUsername("trainer.test")).thenReturn(Optional.of(matchedUser));

            // Must execute cleanly without propagating runtime exceptions
            securityAspect.authenticate(joinPoint, requiresAuth);

            verify(userRepository, times(1)).findByUsername("trainer.test");
        }
    }

    @Nested
    @DisplayName("Authentication Failure Tests")
    class FailureTests {

        @Test
        @DisplayName("Should throw AuthenticationException when JoinPoint contains no AuthData payload instance")
        void authenticate_MissingAuthData_ThrowsAuthenticationException() {
            Object[] mixedArgsWithoutAuth = new Object[]{"some String argument", 42, new Object()};
            when(joinPoint.getArgs()).thenReturn(mixedArgsWithoutAuth);

            AuthenticationException exception = assertThrows(AuthenticationException.class, () ->
                    securityAspect.authenticate(joinPoint, requiresAuth)
            );

            assertEquals("Authentication failed", exception.getMessage());
            verifyNoInteractions(userRepository);
        }

        @Test
        @DisplayName("Should throw AuthenticationException when credentials username payload is missing inside data storage")
        void authenticate_UsernameNotFound_ThrowsAuthenticationException() {
            AuthData missingUserAuth = new AuthData();
            missingUserAuth.setUsername("ghost.user");
            missingUserAuth.setPassword("any_pass");

            when(joinPoint.getArgs()).thenReturn(new Object[]{missingUserAuth});
            when(userRepository.findByUsername("ghost.user")).thenReturn(Optional.empty());

            AuthenticationException exception = assertThrows(AuthenticationException.class, () ->
                    securityAspect.authenticate(joinPoint, requiresAuth)
            );

            assertEquals("Authentication failed", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw AuthenticationException when provided payload password mismatches stored database entity password")
        void authenticate_WrongPassword_ThrowsAuthenticationException() {
            AuthData badPasswordAuth = new AuthData();
            badPasswordAuth.setUsername("trainer.test");
            badPasswordAuth.setPassword("wrong_pass");

            User storedUser = User.builder()
                    .id(UUID.randomUUID())
                    .username("trainer.test")
                    .password("actual_secure_pass")
                    .build();

            when(joinPoint.getArgs()).thenReturn(new Object[]{badPasswordAuth});
            when(userRepository.findByUsername("trainer.test")).thenReturn(Optional.of(storedUser));

            AuthenticationException exception = assertThrows(AuthenticationException.class, () ->
                    securityAspect.authenticate(joinPoint, requiresAuth)
            );

            assertEquals("Authentication failed", exception.getMessage());
        }
    }
}