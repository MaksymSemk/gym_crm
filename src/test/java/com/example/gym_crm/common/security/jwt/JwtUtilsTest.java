package com.example.gym_crm.common.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(
                "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
                3600000
        );
    }

    @Test
    void generateAndValidateToken_Success() {
        UserDetails userDetails = User.withUsername("john.doe")
                .password("pass")
                .roles("TRAINEE")
                .build();

        String token = jwtUtils.generateToken(userDetails);

        assertThat(jwtUtils.validateToken(token)).isTrue();
        assertThat(jwtUtils.getUsernameFromToken(token)).isEqualTo("john.doe");
    }

    @Test
    void validateToken_ExpiredToken_ReturnsFalse() {
        JwtUtils shortLivedUtils = new JwtUtils(
                "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970", 1
        );
        UserDetails userDetails = User.withUsername("john.doe").password("p").roles("USER").build();
        String token = shortLivedUtils.generateToken(userDetails);

        assertThat(shortLivedUtils.validateToken(token)).isFalse();
    }
}