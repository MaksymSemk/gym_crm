package com.example.trainerworkloadservice.logging;

import com.example.grpc.common.GrpcLoggingConstants;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@GlobalServerInterceptor
public class GrpcServerLoggingInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String txId = headers.get(GrpcLoggingConstants.TRANSACTION_ID_HEADER);
        if (txId == null || txId.isBlank()) {
            txId = UUID.randomUUID().toString();
        }
        MDC.put(GrpcLoggingConstants.MDC_TRANSACTION_ID_KEY, txId);
        final String currentTxId = txId;

        ServerCall<ReqT, RespT> forwardingCall = new ForwardingServerCall.SimpleForwardingServerCall<>(call) {
            @Override
            public void sendMessage(RespT message) {
                MDC.put(GrpcLoggingConstants.MDC_TRANSACTION_ID_KEY, currentTxId);
                log.info("INCOMING gRPC [Response Payload] -> Endpoint: {} | Payload: {}",
                        call.getMethodDescriptor().getFullMethodName(), message);
                super.sendMessage(message);
            }

            @Override
            public void close(Status status, Metadata trailers) {
                MDC.put(GrpcLoggingConstants.MDC_TRANSACTION_ID_KEY, currentTxId);
                if (status.isOk()) {
                    log.info("INCOMING gRPC [Transaction Finish] -> Endpoint: {} | Status: 200 OK",
                            call.getMethodDescriptor().getFullMethodName());
                } else {
                    log.error("INCOMING gRPC [Transaction Finish] -> Endpoint: {} | Status Error: {} | Message: {}",
                            call.getMethodDescriptor().getFullMethodName(), status.getCode(), status.getDescription());
                }
                try {
                    super.close(status, trailers);
                } finally {
                    MDC.remove(GrpcLoggingConstants.MDC_TRANSACTION_ID_KEY);
                }
            }
        };

        ServerCall.Listener<ReqT> listener = next.startCall(forwardingCall, headers);

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(listener) {
            @Override
            public void onMessage(ReqT message) {
                MDC.put(GrpcLoggingConstants.MDC_TRANSACTION_ID_KEY, currentTxId);
                log.info("INCOMING gRPC [Transaction Start] -> Endpoint: {} | Request Payload: {}",
                        call.getMethodDescriptor().getFullMethodName(), message);
                super.onMessage(message);
            }
        };
    }
}