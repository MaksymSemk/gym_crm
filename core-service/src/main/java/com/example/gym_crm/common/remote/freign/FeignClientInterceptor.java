package com.example.gym_crm.common.remote.freign;

import com.example.gym_crm.common.logging.TransactionContextStorage;
import com.example.gym_crm.common.logging.TransactionLoggingFilter;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
public class FeignClientInterceptor implements RequestInterceptor {

    private final TransactionContextStorage contextStorage;

    @Override
    public void apply(RequestTemplate template) {
        String requestId = null;

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            requestId = (String) request.getAttribute(TransactionLoggingFilter.REQ_ID_ATTRIBUTE);

            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                template.header("Authorization", authHeader);
            }
        }

        String txId = contextStorage.getTransactionId(requestId);
        if (txId != null && !txId.isBlank()) {
            template.header(TransactionContextStorage.TRANSACTION_ID_HEADER, txId);
        }
    }
}