package com.example.gym_crm.common.remote.grpc;

import com.example.grpc.common.GrpcLoggingConstants;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class GrpcClientLoggingInterceptor implements ClientInterceptor {

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {

        return new ForwardingClientCall.SimpleForwardingClientCall<>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                // 1. Generate or extract transaction ID
                String txId = MDC.get(GrpcLoggingConstants.MDC_TRANSACTION_ID_KEY);
                if (txId == null || txId.isBlank()) {
                    txId = UUID.randomUUID().toString();
                    MDC.put(GrpcLoggingConstants.MDC_TRANSACTION_ID_KEY, txId);
                }
                headers.put(GrpcLoggingConstants.TRANSACTION_ID_HEADER, txId);
                final String currentTxId = txId;

                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<>(responseListener) {
                    @Override
                    public void onMessage(RespT message) {
                        MDC.put(GrpcLoggingConstants.MDC_TRANSACTION_ID_KEY, currentTxId);
                        log.info("OUTGOING gRPC [Response Body] -> Endpoint: {} | Payload: {}",
                                method.getFullMethodName(), message);
                        super.onMessage(message);
                    }

                    @Override
                    public void onClose(Status status, Metadata trailers) {
                        MDC.put(GrpcLoggingConstants.MDC_TRANSACTION_ID_KEY, currentTxId);
                        if (status.isOk()) {
                            log.info("OUTGOING gRPC [Transaction Finish] -> Endpoint: {} | Status: 200 OK",
                                    method.getFullMethodName());
                        } else {
                            log.error("OUTGOING gRPC [Transaction Finish] -> Endpoint: {} | Status Error: {} | Details: {}",
                                    method.getFullMethodName(), status.getCode(), status.getDescription());
                        }
                        super.onClose(status, trailers);
                    }
                }, headers);
            }

            @Override
            public void sendMessage(ReqT message) {
                String txId = MDC.get(GrpcLoggingConstants.MDC_TRANSACTION_ID_KEY);
                log.info("OUTGOING gRPC [Transaction Start] -> Endpoint: {} | Request Payload: {}",
                        method.getFullMethodName(), message);
                super.sendMessage(message);
            }
        };
    }
}