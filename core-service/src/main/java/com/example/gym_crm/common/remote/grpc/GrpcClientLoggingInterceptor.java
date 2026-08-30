package com.example.gym_crm.common.remote.grpc;

import com.example.grpc.common.GrpcLoggingConstants;
import com.example.gym_crm.common.logging.TransactionContextStorage;
import com.example.gym_crm.common.logging.TransactionLoggingFilter;
import io.grpc.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class GrpcClientLoggingInterceptor implements ClientInterceptor {

    private final TransactionContextStorage contextStorage;

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {

        String requestId = null;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Object reqIdAttr = request.getAttribute(TransactionLoggingFilter.REQ_ID_ATTRIBUTE);
            requestId = (reqIdAttr != null) ? reqIdAttr.toString() : request.getRequestId();
        }

        String resolvedTxId = contextStorage.getTransactionId(requestId);
        if (resolvedTxId == null || resolvedTxId.isBlank()) {
            resolvedTxId = UUID.randomUUID().toString();
        }
        final String effectiveTxId = resolvedTxId;

        return new ForwardingClientCall.SimpleForwardingClientCall<>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                headers.put(GrpcLoggingConstants.TRANSACTION_ID_HEADER, effectiveTxId);

                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<>(responseListener) {
                    @Override
                    public void onMessage(RespT message) {
                        MDC.put(GrpcLoggingConstants.MDC_TRANSACTION_ID_KEY, effectiveTxId);
                        try {
                            log.info("OUTGOING gRPC [Response Body] -> Endpoint: {} | Payload: {}",
                                    method.getFullMethodName(), message);
                            super.onMessage(message);
                        } finally {
                            MDC.remove(GrpcLoggingConstants.MDC_TRANSACTION_ID_KEY);
                        }
                    }

                    @Override
                    public void onClose(Status status, Metadata trailers) {
                        MDC.put(GrpcLoggingConstants.MDC_TRANSACTION_ID_KEY, effectiveTxId);
                        try {
                            if (status.isOk()) {
                                log.info("OUTGOING gRPC [Transaction Finish] -> Endpoint: {} | Status: 200 OK",
                                        method.getFullMethodName());
                            } else {
                                log.error("OUTGOING gRPC [Transaction Finish] -> Endpoint: {} | Status Error: {} | Details: {}",
                                        method.getFullMethodName(), status.getCode(), status.getDescription());
                            }
                            super.onClose(status, trailers);
                        } finally {
                            MDC.remove(GrpcLoggingConstants.MDC_TRANSACTION_ID_KEY);
                        }
                    }
                }, headers);
            }

            @Override
            public void sendMessage(ReqT message) {
                MDC.put(GrpcLoggingConstants.MDC_TRANSACTION_ID_KEY, effectiveTxId);
                try {
                    log.info("OUTGOING gRPC [Transaction Start] -> Endpoint: {} | Request Payload: {}",
                            method.getFullMethodName(), message);
                    super.sendMessage(message);
                } finally {
                    MDC.remove(GrpcLoggingConstants.MDC_TRANSACTION_ID_KEY);
                }
            }
        };
    }
}