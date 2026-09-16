package com.example.integrationtests.integration;

import com.example.gym_crm.common.security.jwt.JwtUtils;
import com.example.gym_crm.common.user.Role;
import com.example.gym_crm.common.user.User;
import com.example.gym_crm.trainee.Trainee;
import com.example.gym_crm.trainee.repository.TraineeRepository;
import com.example.gym_crm.trainer.Trainer;
import com.example.gym_crm.trainer.repository.TrainerRepository;
import com.example.gym_crm.training.Dto.TrainingCreateDto;
import com.example.gym_crm.training_type.TrainingType;
import com.example.gym_crm.training_type.repository.TrainingTypeRepository;
import com.example.trainerworkloadservice.workload.model.MonthSummary;
import com.example.trainerworkloadservice.workload.model.TrainerWorkload;
import com.example.trainerworkloadservice.workload.model.YearSummary;
import com.example.trainerworkloadservice.workload.repository.TrainerWorkloadRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IntegrationSteps {

    private final MockMvc mockMvc;
    private final JdbcTemplate jdbcTemplate;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainerWorkloadRepository mongoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String kafkaBootstrapServers;

    private ResultActions lastResultActions;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public IntegrationSteps(WebApplicationContext webApplicationContext,
                            JdbcTemplate jdbcTemplate,
                            TraineeRepository traineeRepository,
                            TrainerRepository trainerRepository,
                            TrainingTypeRepository trainingTypeRepository,
                            TrainerWorkloadRepository mongoRepository,
                            PasswordEncoder passwordEncoder,
                            JwtUtils jwtUtils,
                            KafkaTemplate<String, Object> kafkaTemplate,
                            @Value("${spring.kafka.bootstrap-servers}") String kafkaBootstrapServers) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        this.jdbcTemplate = jdbcTemplate;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.mongoRepository = mongoRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaBootstrapServers = kafkaBootstrapServers;
    }

    @Given("a clean state across all services")
    public void cleanStateAcrossServices() {
        jdbcTemplate.execute("DELETE FROM trainings");
        jdbcTemplate.execute("DELETE FROM trainer_trainee");
        jdbcTemplate.execute("DELETE FROM trainees");
        jdbcTemplate.execute("DELETE FROM trainers");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM training_types");

        mongoRepository.deleteAll();
    }

    @Given("a training type exists with id {long} and name {string}")
    public void trainingTypeExists(Long id, String name) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM training_types WHERE id = ?", Integer.class, id);
        if (count == null || count == 0) {
            jdbcTemplate.update(
                    "INSERT INTO training_types (id, name) VALUES (?, ?)", id, name);
        }
    }

    @Given("a registered trainer exists in gym CRM:")
    public void registeredTrainerExists(DataTable dataTable) {
        Map<String, String> row = dataTable.asMaps().get(0);
        TrainingType type = trainingTypeRepository.findById(Long.parseLong(row.get("specializationId"))).orElseThrow();

        User user = User.builder()
                .firstName(row.get("firstName"))
                .lastName(row.get("lastName"))
                .username(row.get("username"))
                .password(passwordEncoder.encode("secret123"))
                .role(Role.TRAINER)
                .isActive(true)
                .build();

        Trainer trainer = Trainer.builder()
                .user(user)
                .specialization(type)
                .trainees(new ArrayList<>())
                .trainings(new ArrayList<>())
                .build();

        trainerRepository.save(trainer);
    }

    @Given("a registered trainee exists in gym CRM:")
    public void registeredTraineeExists(DataTable dataTable) {
        Map<String, String> row = dataTable.asMaps().get(0);

        User user = User.builder()
                .firstName(row.get("firstName"))
                .lastName(row.get("lastName"))
                .username(row.get("username"))
                .password(passwordEncoder.encode("secret123"))
                .role(Role.TRAINEE)
                .isActive(true)
                .build();

        Trainee trainee = Trainee.builder()
                .user(user)
                .dateOfBirth(LocalDate.of(1998, 3, 10))
                .address("456 Central Ave")
                .trainers(new ArrayList<>())
                .trainings(new ArrayList<>())
                .build();

        traineeRepository.save(trainee);
    }

    @When("a training session is created via gym CRM:")
    public void createTrainingSession(DataTable dataTable) throws Exception {
        Map<String, String> row = dataTable.asMaps().get(0);

        TrainingCreateDto dto = new TrainingCreateDto(
                row.get("traineeUsername"),
                row.get("trainerUsername"),
                row.get("trainingName"),
                LocalDate.parse(row.get("trainingDate")),
                Integer.parseInt(row.get("trainingDuration"))
        );

        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        row.get("traineeUsername"),
                        "secret123",
                        List.of(new SimpleGrantedAuthority("ROLE_TRAINEE"))
                );
        String token = jwtUtils.generateToken(principal);

        lastResultActions = mockMvc.perform(post("/api/v1/trainings")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
    }

    @Then("the CRM response status code should be {int}")
    public void crmResponseStatusCodeShouldBe(int expectedStatus) throws Exception {
        lastResultActions.andExpect(status().is(expectedStatus));
    }

    @When("a corrupted workload message with invalid JSON payload is published to topic {string}")
    public void publishCorruptedMessage(String topic) throws Exception {
        // Send and block with .get() to guarantee the broker received the corrupted message
        kafkaTemplate.send(topic, "corrupted-key", "MALFORMED_NON_JSON_PAYLOAD").get(5, TimeUnit.SECONDS);
    }

    @Then("within {int} seconds MongoDB should record a workload duration of {int} for trainer {string} in year {int} month {int}")
    public void verifyWorkloadInMongo(int timeoutSec, int expectedDuration, String username, int year, int month) {
        await().atMost(timeoutSec, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    Optional<TrainerWorkload> optionalWorkload = mongoRepository.findByTrainerUsername(username);
                    assertThat(optionalWorkload).isPresent();

                    TrainerWorkload workload = optionalWorkload.get();
                    YearSummary yearSummary = workload.getYears().stream()
                            .filter(y -> y.getYear() == year)
                            .findFirst()
                            .orElseThrow(() -> new AssertionError("Year " + year + " not found"));

                    MonthSummary monthSummary = yearSummary.getMonths().stream()
                            .filter(m -> m.getMonthNumber() == month)
                            .findFirst()
                            .orElseThrow(() -> new AssertionError("Month " + month + " not found"));

                    assertThat(monthSummary.getTrainingSummaryDuration()).isEqualTo(expectedDuration);
                });
    }

    @Then("within {int} seconds the message should arrive in dead letter topic {string}")
    public void verifyMessageInDlt(int timeoutSec, String dltTopic) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "dlt-verifier-group-" + UUID.randomUUID());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        DefaultKafkaConsumerFactory<String, String> factory = new DefaultKafkaConsumerFactory<>(props);
        try (Consumer<String, String> consumer = factory.createConsumer()) {
            consumer.subscribe(Collections.singletonList(dltTopic));
            await().atMost(timeoutSec, TimeUnit.SECONDS)
                    .pollInterval(300, TimeUnit.MILLISECONDS)
                    .untilAsserted(() -> {
                        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(250));
                        boolean recordFound = false;
                        for (ConsumerRecord<String, String> record : records) {
                            boolean keyMatches = "corrupted-key".equals(record.key());
                            boolean valMatches = record.value() != null && record.value().contains("MALFORMED_NON_JSON_PAYLOAD");
                            if (keyMatches || valMatches) {
                                recordFound = true;
                                break;
                            }
                        }
                        assertThat(recordFound)
                                .withFailMessage("Expected record with key 'corrupted-key' in topic %s", dltTopic)
                                .isTrue();
                    });
        }
    }

    @Then("no workload records should exist in MongoDB for trainer {string}")
    public void noWorkloadRecordsExist(String username) {
        assertThat(mongoRepository.findByTrainerUsername(username)).isEmpty();
    }
}