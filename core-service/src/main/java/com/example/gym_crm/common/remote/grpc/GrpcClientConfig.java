package com.example.gym_crm.common.remote.grpc;


import com.example.grpc.workload.TrainerWorkloadGrpcServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Bean
    public ManagedChannel workloadGrpcChannel(
            @Value("${grpc.server.host:localhost}") String host,
            @Value("${grpc.server.port:9090}") int port
    ) {
        return ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
    }

    @Bean
    public TrainerWorkloadGrpcServiceGrpc.TrainerWorkloadGrpcServiceBlockingStub trainerWorkloadGrpcServiceBlockingStub(
            ManagedChannel workloadGrpcChannel,
            GrpcClientLoggingInterceptor interceptor) {
        return TrainerWorkloadGrpcServiceGrpc.newBlockingStub(workloadGrpcChannel)
                .withInterceptors(interceptor);
    }
}