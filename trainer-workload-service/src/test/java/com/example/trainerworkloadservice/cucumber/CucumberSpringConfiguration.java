package com.example.trainerworkloadservice.cucumber;

import com.example.trainerworkloadservice.TrainerWorkloadServiceApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

@CucumberContextConfiguration
@SpringBootTest(classes = TrainerWorkloadServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CucumberSpringConfiguration {

    protected static final MongoDBContainer mongoContainer = new MongoDBContainer("mongo:latest");

    static {
        mongoContainer.start();
    }

    @DynamicPropertySource
    static void configureMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", mongoContainer::getReplicaSetUrl);

        registry.add("spring.kafka.listener.auto-startup", () -> "false");
        registry.add("eureka.client.enabled", () -> "false");
    }
}