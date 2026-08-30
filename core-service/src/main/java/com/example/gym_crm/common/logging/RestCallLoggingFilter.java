package com.example.gym_crm.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Component
@Slf4j
public class RestCallLoggingFilter extends OncePerRequestFilter {

    private static final int MAX_PAYLOAD_LENGTH = 1000;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, MAX_PAYLOAD_LENGTH);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            logRestCall(wrappedRequest, wrappedResponse);
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRestCall(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullPath = queryString != null ? uri + "?" + queryString : uri;

        String incomingPayload = getIncomingPayload(request);
        String outgoingPayload = getOutgoingPayload(response);

        if (incomingPayload.isBlank()) {
            log.info("INCOMING REST [{} {}]\n" +
                            "-> OUTGOING REST Status: {} | Payload: {}",
                    request.getMethod(), sanitizeUrl(fullPath), response.getStatus(), outgoingPayload);
        } else {
            log.info("INCOMING REST [{} {}] | Payload: {}\n" +
                            "-> OUTGOING REST Status: {} | Payload: {}",
                    request.getMethod(), sanitizeUrl(fullPath), sanitizePayload(incomingPayload), response.getStatus(), outgoingPayload);
        }
    }

    private String getIncomingPayload(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        return extractString(buf, request.getCharacterEncoding());
    }

    private String getOutgoingPayload(ContentCachingResponseWrapper response) {
        byte[] buf = response.getContentAsByteArray();
        return extractString(buf, response.getCharacterEncoding());
    }

    private String extractString(byte[] buf, String encoding) {
        if (buf.length > 0) {
            int length = Math.min(buf.length, MAX_PAYLOAD_LENGTH);
            try {
                String charset = encoding != null ? encoding : "UTF-8";
                String payload = new String(buf, 0, length, charset);
                return sanitizePayload(payload);
            } catch (Exception e) {
                return "[Unknown Encoding]";
            }
        }
        return "[Empty]";
    }

    private String sanitizePayload(String payload) {
        String singleLine = payload.replaceAll("[\\r\\n]+", " ");
        return singleLine.replaceAll("(?i)(\"password\"\\s*:\\s*\")[^\"]+(\")", "$1*****$2");
    }

    private String sanitizeUrl(String fullPath) {
        if (fullPath == null || !fullPath.contains("?")) {
            return fullPath;
        }
        return fullPath.replaceAll("(?i)([?&](?:password|token|secret|key)=)[^&]+", "$1*****");
    }
}