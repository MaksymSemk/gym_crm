package com.example.gym_crm.common.logging;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TransactionContextStorage {

    public static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";
    public static final String MDC_KEY = "transactionId";

    private final Map<String, String> transactionRegistry = new ConcurrentHashMap<>();

    public String init(String requestId, String incomingId) {
        String txId = (incomingId != null && !incomingId.isBlank()) ? incomingId : UUID.randomUUID().toString();
        if (requestId != null && !requestId.isBlank()) {
            transactionRegistry.put(requestId, txId);
        }
        MDC.put(MDC_KEY, txId);
        return txId;
    }

    public String getTransactionId(String requestId) {
        if (requestId != null && transactionRegistry.containsKey(requestId)) {
            return transactionRegistry.get(requestId);
        }
        return MDC.get(MDC_KEY);
    }

    public String getTransactionId() {
        return getTransactionId(null);
    }

    public void clear(String requestId) {
        if (requestId != null) {
            transactionRegistry.remove(requestId);
        }
        MDC.remove(MDC_KEY);
    }

    public void clear() {
        clear(null);
    }
}