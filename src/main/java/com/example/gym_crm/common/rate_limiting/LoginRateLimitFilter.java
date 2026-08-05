package com.example.gym_crm.common.rate_limiting;

import com.example.gym_crm.common.get_ip.GetIpService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = 300_000; // 5 minutes

    private final GetIpService getIpService;
    private final Map<String, LoginAttempt> attemptsMap = new ConcurrentHashMap<>();

    private static class LoginAttempt {
        private int count;
        private long lastAttemptTime;

        LoginAttempt(long now) {
            this.count = 1;
            this.lastAttemptTime = now;
        }

        synchronized void increment(long now) {
            this.count++;
            this.lastAttemptTime = now;
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !("/api/v1/auth/login".equalsIgnoreCase(request.getRequestURI())
                && "GET".equalsIgnoreCase(request.getMethod()));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String clientIp = getIpService.getRemoteIP(request);
        long now = System.currentTimeMillis();

        LoginAttempt attempt = attemptsMap.get(clientIp);
        if (attempt != null) {
            if (now - attempt.lastAttemptTime >= LOCKOUT_DURATION_MS) {
                log.debug("Lockout duration expired for IP {}. Resetting attempt counter.", clientIp);
                attemptsMap.remove(clientIp);
            } else if (attempt.count >= MAX_FAILED_ATTEMPTS) {
                log.warn("Rate limit exceeded for IP {}. Request blocked (Attempt {}/{}).",
                        clientIp, attempt.count, MAX_FAILED_ATTEMPTS);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("""
                        {"error":"Too many failed login attempts. Account locked for 5 minutes."}
                        """);
                return;
            }
        }

        filterChain.doFilter(request, response);

        if (response.getStatus() == HttpStatus.UNAUTHORIZED.value()) {
            attemptsMap.compute(clientIp, (key, existing) -> {
                if (existing == null || (now - existing.lastAttemptTime >= LOCKOUT_DURATION_MS)) {
                    log.debug("First failed login attempt registered for IP {}", clientIp);
                    return new LoginAttempt(now);
                }
                existing.increment(now);
                log.debug("Incremented failed login attempt for IP {}: count={}", clientIp, existing.count);
                return existing;
            });
        } else if (response.getStatus() == HttpStatus.OK.value()) {
            attemptsMap.remove(clientIp);
        }
    }
}