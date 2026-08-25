package com.example.gym_crm.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
@RequiredArgsConstructor
public class TransactionLoggingFilter extends OncePerRequestFilter {

    public static final String REQ_ID_ATTRIBUTE = "APP_REQUEST_ID";
    private final TransactionContextStorage contextStorage;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestId = request.getRequestId();
        request.setAttribute(REQ_ID_ATTRIBUTE, requestId);

        String incomingTxId = request.getHeader(TransactionContextStorage.TRANSACTION_ID_HEADER);
        String txId = contextStorage.init(requestId, incomingTxId);

        response.setHeader(TransactionContextStorage.TRANSACTION_ID_HEADER, txId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            contextStorage.clear(txId);
        }
    }
}