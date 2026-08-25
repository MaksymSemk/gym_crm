package com.example.grpc.common;

import io.grpc.Metadata;

public final class GrpcLoggingConstants {
    public static final String MDC_TRANSACTION_ID_KEY = "transactionId";
    public static final Metadata.Key<String> TRANSACTION_ID_HEADER =
            Metadata.Key.of("X-Transaction-Id", Metadata.ASCII_STRING_MARSHALLER);

    private GrpcLoggingConstants() {}
}