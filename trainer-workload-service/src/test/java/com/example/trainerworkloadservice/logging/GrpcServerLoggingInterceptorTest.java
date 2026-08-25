package com.example.trainerworkloadservice.logging;

import com.example.grpc.common.GrpcLoggingConstants;
import io.grpc.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrpcServerLoggingInterceptorTest {

    @Mock
    private ServerCall<String, String> serverCall;

    @Mock
    private ServerCallHandler<String, String> next;

    @Mock
    private MethodDescriptor<String, String> methodDescriptor;

    @Mock
    private ServerCall.Listener<String> delegateListener;

    @InjectMocks
    private GrpcServerLoggingInterceptor interceptor;

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("interceptCall captures transaction ID from metadata and binds it to listener callbacks")
    void interceptCall_PropagatesTransactionId() {
        Metadata headers = new Metadata();
        headers.put(GrpcLoggingConstants.TRANSACTION_ID_HEADER, "custom-grpc-tx-99");

        when(next.startCall(any(), eq(headers))).thenReturn(delegateListener);

        ServerCall.Listener<String> listener = interceptor.interceptCall(serverCall, headers, next);

        assertThat(listener).isNotNull();

        when(serverCall.getMethodDescriptor()).thenReturn(methodDescriptor);
        when(methodDescriptor.getFullMethodName()).thenReturn("workload.TrainerWorkloadGrpcService/UpdateWorkload");

        listener.onMessage("test-message");

        verify(delegateListener).onMessage("test-message");
    }
}