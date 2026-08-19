package com.example.gym_crm.common.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtBlacklistServiceTest {

    private JwtBlacklistService blacklistService;

    @BeforeEach
    void setUp() {
        blacklistService = new JwtBlacklistService();
    }

    @Test
    @DisplayName("Should return false when token has not been blacklisted")
    void isBlacklisted_NonBlacklistedToken_ReturnsFalse() {
        boolean result = blacklistService.isBlacklisted("valid.jwt.token");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return true after token has been explicitly blacklisted")
    void blacklistToken_AddsTokenToBlacklist() {
        String token = "sample.jwt.token";

        blacklistService.blacklistToken(token);

        assertThat(blacklistService.isBlacklisted(token)).isTrue();
    }

    @Test
    @DisplayName("Should handle multiple tokens independently")
    void isBlacklisted_MultipleTokens_TracksIndependently() {
        String token1 = "token.one";
        String token2 = "token.two";

        blacklistService.blacklistToken(token1);

        assertThat(blacklistService.isBlacklisted(token1)).isTrue();
        assertThat(blacklistService.isBlacklisted(token2)).isFalse();
    }
}