package com.example.gym_crm.common.remote.freign;

import com.example.gym_crm.common.logging.TransactionLoggingFilter;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // 1. Propagate Transaction ID from MDC
        String transactionId = MDC.get(TransactionLoggingFilter.MDC_TRANSACTION_ID_KEY);
        if (transactionId != null) {
            template.header(TransactionLoggingFilter.TRANSACTION_ID_HEADER, transactionId);
        }

        // 2. Propagate Bearer JWT from incoming HTTP request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                template.header("Authorization", authHeader);
            }
        }
    }
}