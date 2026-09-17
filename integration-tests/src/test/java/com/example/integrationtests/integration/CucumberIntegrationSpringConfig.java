package com.example.integrationtests.integration;

import com.example.gym_crm.training.remote.dto.TrainerWorkloadRequest;
import io.cucumber.spring.CucumberContextConfiguration;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

@CucumberContextConfiguration
@SpringBootTest(
        classes = CucumberIntegrationSpringConfig.TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("integration-test")
public class CucumberIntegrationSpringConfig {

    protected static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15-alpine");
    protected static final MongoDBContainer mongo =
            new MongoDBContainer("mongo:latest");
    protected static final KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    static {
        postgres.start();
        mongo.start();
        kafka.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.mongodb.uri", mongo::getReplicaSetUrl);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("kafka.topics.trainer-workload", () -> "trainer-workload-topic");
        registry.add("kafka.topics.trainer-workload-dlt", () -> "trainer-workload-topic.DLT");
    }

    @SpringBootApplication
    @EnableJpaRepositories(basePackages = "com.example.gym_crm")
    @EntityScan(basePackages = "com.example.gym_crm")
    @EnableMongoRepositories(basePackages = "com.example.trainerworkloadservice.workload.repository")
    @ComponentScan(
            basePackages = {
                    "com.example.gym_crm",
                    "com.example.trainerworkloadservice.config",
                    "com.example.trainerworkloadservice.workload.kafka",
                    "com.example.trainerworkloadservice.workload",
                    "com.example.integrationtests.integration"
            },
            excludeFilters = {
                    @ComponentScan.Filter(
                            type = FilterType.REGEX,
                            pattern = "com\\.example\\.gym_crm\\.cucumber\\..*"
                    )
            }
    )
    public static class TestApplication {

        @Bean
        public ProducerFactory<String, Object> testProducerFactory(
                @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
            return new DefaultKafkaProducerFactory<>(configProps);
        }

        @Bean
        public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> testProducerFactory) {
            return new KafkaTemplate<>(testProducerFactory);
        }

        @Bean
        @Primary
        @SuppressWarnings("unchecked")
        public KafkaTemplate<String, TrainerWorkloadRequest> trainerWorkloadKafkaTemplate(
                KafkaTemplate<String, Object> kafkaTemplate) {
            return (KafkaTemplate<String, TrainerWorkloadRequest>) (KafkaTemplate<?, ?>) kafkaTemplate;
        }
    }
}