package com.example.trainerworkloadservice.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionLoggingFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private TransactionLoggingFilter filter;

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("doFilterInternal sets MDC and header with existing X-Transaction-Id")
    void doFilterInternal_ExistingHeader_Propagates() throws ServletException, IOException {
        when(request.getHeader(TransactionLoggingFilter.TRANSACTION_ID_HEADER)).thenReturn("tx-12345");

        doAnswer(invocation -> {
            assertThat(MDC.get(TransactionLoggingFilter.MDC_TRANSACTION_ID_KEY)).isEqualTo("tx-12345");
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setHeader(TransactionLoggingFilter.TRANSACTION_ID_HEADER, "tx-12345");
        assertThat(MDC.get(TransactionLoggingFilter.MDC_TRANSACTION_ID_KEY)).isNull();
    }

    @Test
    @DisplayName("doFilterInternal generates UUID when header is missing")
    void doFilterInternal_MissingHeader_GeneratesNew() throws ServletException, IOException {
        when(request.getHeader(TransactionLoggingFilter.TRANSACTION_ID_HEADER)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setHeader(eq(TransactionLoggingFilter.TRANSACTION_ID_HEADER), anyString());
        assertThat(MDC.get(TransactionLoggingFilter.MDC_TRANSACTION_ID_KEY)).isNull();
    }
}