package com.example.gym_crm.cucumber;

import com.example.gym_crm.GymCrmApplication;
import com.example.gym_crm.training.remote.kafka.TrainerWorkloadProducer;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;

@CucumberContextConfiguration
@SpringBootTest(classes = GymCrmApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CucumberSpringConfiguration {

    // Singleton container shared across all Cucumber feature executions
    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("gym_crm_cucumber_db")
            .withUsername("test_user")
            .withPassword("test_pass");

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.sql.init.mode", () -> "never");
        // Disable Kafka publishing inside component tests by default
        registry.add("spring.kafka.listener.auto-startup", () -> "false");
    }

    @MockitoBean
    protected TrainerWorkloadProducer trainerWorkloadProducer;
}