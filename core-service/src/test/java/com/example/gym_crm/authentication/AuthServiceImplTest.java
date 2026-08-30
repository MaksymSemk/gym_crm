package com.example.gym_crm.authentication;

import com.example.gym_crm.authentication.dto.AuthResponseDto;
import com.example.gym_crm.authentication.dto.ChangePasswordRequestDto;
import com.example.gym_crm.authentication.dto.LoginRequestDto;
import com.example.gym_crm.common.exception.EntityDoesNotExistException;
import com.example.gym_crm.common.security.jwt.JwtBlacklistService;
import com.example.gym_crm.common.security.jwt.JwtUtils;
import com.example.gym_crm.common.user.User;
import com.example.gym_crm.common.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private JwtBlacklistService blacklistService;

    @InjectMocks
    private AuthServiceImpl authService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("login()")
    class LoginTests {

        @Test
        @DisplayName("Should return AuthResponseDto with token when credentials are valid")
        void login_Success() {
            LoginRequestDto dto = new LoginRequestDto("john.doe", "password123");

            UserDetails userDetails = mock(UserDetails.class);
            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
            when(jwtUtils.generateToken(userDetails)).thenReturn("mocked-jwt-token");

            AuthResponseDto result = authService.login(dto);

            assertThat(result).isNotNull();
            assertThat(result.username()).isEqualTo("john.doe");
            assertThat(result.token()).isEqualTo("mocked-jwt-token");
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtUtils).generateToken(userDetails);
        }

        @Test
        @DisplayName("Should propagate BadCredentialsException when authentication fails")
        void login_BadCredentials_ThrowsException() {
            LoginRequestDto dto = new LoginRequestDto("john.doe", "wrongPassword");
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Invalid credentials"));

            assertThatThrownBy(() -> authService.login(dto))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("Invalid credentials");

            verify(jwtUtils, never()).generateToken(any());
        }
    }

    @Nested
    @DisplayName("changePassword()")
    class ChangePasswordTests {

        @Test
        @DisplayName("Should update password successfully when user exists and old password matches")
        void changePassword_Success() {
            ChangePasswordRequestDto dto = new ChangePasswordRequestDto("john.doe", "oldPass", "newPass");
            User user = new User();
            user.setUsername("john.doe");
            user.setPassword("encodedOldPass");

            when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("oldPass", "encodedOldPass")).thenReturn(true);
            when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

            authService.changePassword(dto);

            assertThat(user.getPassword()).isEqualTo("encodedNewPass");
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("Should throw EntityDoesNotExistException when user is not found")
        void changePassword_UserNotFound_ThrowsException() {
            ChangePasswordRequestDto dto = new ChangePasswordRequestDto("nonexistent", "oldPass", "newPass");
            when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.changePassword(dto))
                    .isInstanceOf(EntityDoesNotExistException.class)
                    .hasMessage("User not found: nonexistent");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BadCredentialsException when old password does not match")
        void changePassword_InvalidOldPassword_ThrowsException() {
            ChangePasswordRequestDto dto = new ChangePasswordRequestDto("john.doe", "wrongOldPass", "newPass");
            User user = new User();
            user.setUsername("john.doe");
            user.setPassword("encodedOldPass");

            when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrongOldPass", "encodedOldPass")).thenReturn(false);

            assertThatThrownBy(() -> authService.changePassword(dto))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("Old password does not match");

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("logout()")
    class LogoutTests {

        @Test
        @DisplayName("Should blacklist token and clear security context when Bearer header is present")
        void logout_WithValidBearerHeader_BlacklistsTokenAndClearsContext() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getHeader("Authorization")).thenReturn("Bearer token123");

            authService.logout(request);

            verify(blacklistService).blacklistToken("token123");
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }

        @Test
        @DisplayName("Should clear security context without blacklisting when Authorization header is missing or non-Bearer")
        void logout_WithoutBearerHeader_ClearsContextOnly() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getHeader("Authorization")).thenReturn(null);

            authService.logout(request);

            verify(blacklistService, never()).blacklistToken(any());
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }
    }
}