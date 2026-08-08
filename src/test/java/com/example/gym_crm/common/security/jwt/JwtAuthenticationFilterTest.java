package com.example.gym_crm.common.security.jwt;

import com.example.gym_crm.common.security.CustomUserDetailsService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtBlacklistService blacklistService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should pass through without setting SecurityContext when Authorization header is missing")
    void doFilterInternal_NoAuthHeader_PassesThrough() throws ServletException, IOException {
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(jwtUtils, userDetailsService, blacklistService);
    }

    @Test
    @DisplayName("Should pass through without setting SecurityContext when Header does not start with Bearer")
    void doFilterInternal_NonBearerHeader_PassesThrough() throws ServletException, IOException {
        request.addHeader("Authorization", "Basic dXNlcjpwYXNz");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(jwtUtils, userDetailsService, blacklistService);
    }

    @Test
    @DisplayName("Should not authenticate when token is invalid")
    void doFilterInternal_InvalidToken_PassesThroughWithoutAuth() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer invalid-token");
        when(jwtUtils.validateToken("invalid-token")).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(blacklistService, never()).isBlacklisted(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    @DisplayName("Should not authenticate when token is blacklisted")
    void doFilterInternal_BlacklistedToken_PassesThroughWithoutAuth() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer blacklisted-token");
        when(jwtUtils.validateToken("blacklisted-token")).thenReturn(true);
        when(blacklistService.isBlacklisted("blacklisted-token")).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtUtils, never()).getUsernameFromToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    @DisplayName("Should populate SecurityContext when valid Bearer token is provided")
    void doFilterInternal_ValidToken_AuthenticatesUser() throws ServletException, IOException {
        String token = "valid-token";
        String username = "john.doe";

        request.addHeader("Authorization", "Bearer " + token);

        UserDetails userDetails = new User(
                username,
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_TRAINEE"))
        );

        when(jwtUtils.validateToken(token)).thenReturn(true);
        when(blacklistService.isBlacklisted(token)).thenReturn(false);
        when(jwtUtils.getUsernameFromToken(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo(username);
        assertThat(authentication.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_TRAINEE");
    }

    @Test
    @DisplayName("Should not re-authenticate when SecurityContext already contains an Authentication object")
    void doFilterInternal_AlreadyAuthenticated_SkipsUserLoading() throws ServletException, IOException {
        String token = "valid-token";
        request.addHeader("Authorization", "Bearer " + token);

        // Pre-populate SecurityContext
        UsernamePasswordAuthenticationToken existingAuth = new UsernamePasswordAuthenticationToken(
                "existingUser", null, List.of(new SimpleGrantedAuthority("ROLE_TRAINEE"))
        );
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        when(jwtUtils.validateToken(token)).thenReturn(true);
        when(blacklistService.isBlacklisted(token)).thenReturn(false);
        when(jwtUtils.getUsernameFromToken(token)).thenReturn("john.doe");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("existingUser");
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }
}