package com.example.gym_crm.common.get_ip;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetIpServiceTest {

    private GetIpService getIpService;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        getIpService = new GetIpService();
    }

    @Test
    @DisplayName("Should return remote addr when X-Forwarded-For header is null")
    void getRemoteIP_NullHeader_ReturnsRemoteAddr() {
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");

        String result = getIpService.getRemoteIP(request);

        assertThat(result).isEqualTo("192.168.1.1");
    }

    @Test
    @DisplayName("Should return remote addr when X-Forwarded-For header is empty string")
    void getRemoteIP_EmptyHeader_ReturnsRemoteAddr() {
        when(request.getHeader("X-Forwarded-For")).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        String result = getIpService.getRemoteIP(request);

        assertThat(result).isEqualTo("127.0.0.1");
    }

    @Test
    @DisplayName("Should return single IP directly when X-Forwarded-For contains one IP")
    void getRemoteIP_SingleForwardedIp_ReturnsForwardedIp() {
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.195");

        String result = getIpService.getRemoteIP(request);

        assertThat(result).isEqualTo("203.0.113.195");
    }

    @Test
    @DisplayName("Should extract and trim first IP when X-Forwarded-For contains multiple comma-separated IPs")
    void getRemoteIP_MultipleForwardedIps_ReturnsFirstTrimmedIp() {
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.195, 70.41.3.18, 150.172.238.178");

        String result = getIpService.getRemoteIP(request);

        assertThat(result).isEqualTo("203.0.113.195");
    }
}