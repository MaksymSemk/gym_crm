package com.example.gym_crm.cucumber;

import com.example.gym_crm.common.security.jwt.JwtUtils;
import com.example.gym_crm.common.user.Role;
import com.example.gym_crm.common.user.User;
import com.example.gym_crm.trainee.Trainee;
import com.example.gym_crm.trainee.repository.TraineeRepository;
import com.example.gym_crm.trainer.Trainer;
import com.example.gym_crm.trainer.repository.TrainerRepository;
import com.example.gym_crm.training.Dto.TrainingCreateDto;
import com.example.gym_crm.training.remote.dto.TrainerWorkloadRequest;
import com.example.gym_crm.training.remote.kafka.TrainerWorkloadProducer;
import com.example.gym_crm.training.repository.TrainingRepository;
import com.example.gym_crm.training_type.TrainingType;
import com.example.gym_crm.training_type.repository.TrainingTypeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TrainingSteps {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final PasswordEncoder passwordEncoder;
    private final TrainerWorkloadProducer trainerWorkloadProducer;
    private final JwtUtils jwtUtils;
    private final ScenarioContext context;

    public TrainingSteps(MockMvc mockMvc,
                         TraineeRepository traineeRepository,
                         TrainerRepository trainerRepository,
                         TrainingRepository trainingRepository,
                         TrainingTypeRepository trainingTypeRepository,
                         PasswordEncoder passwordEncoder,
                         TrainerWorkloadProducer trainerWorkloadProducer,
                         JwtUtils jwtUtils,
                         ScenarioContext context) {
        this.mockMvc = mockMvc;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingRepository = trainingRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.passwordEncoder = passwordEncoder;
        this.trainerWorkloadProducer = trainerWorkloadProducer;
        this.jwtUtils = jwtUtils;
        this.context = context;
    }

    @Given("a registered trainer exists:")
    public void registeredTrainerExists(DataTable dataTable) {
        Map<String, String> row = dataTable.asMaps().get(0);
        TrainingType specialization = trainingTypeRepository.findById(Long.parseLong(row.get("specializationId"))).orElseThrow();
        User user = User.builder()
                .firstName(row.get("firstName"))
                .lastName(row.get("lastName"))
                .username(row.get("username"))
                .password(passwordEncoder.encode("password123"))
                .role(Role.TRAINER)
                .isActive(true)
                .build();
        Trainer trainer = Trainer.builder()
                .user(user)
                .specialization(specialization)
                .trainees(new ArrayList<>())
                .trainings(new ArrayList<>())
                .build();
        trainerRepository.save(trainer);
    }

    @Given("a registered trainee exists:")
    public void registeredTraineeExists(DataTable dataTable) {
        Map<String, String> row = dataTable.asMaps().get(0);
        User user = User.builder()
                .firstName(row.get("firstName"))
                .lastName(row.get("lastName"))
                .username(row.get("username"))
                .password(passwordEncoder.encode("password123"))
                .role(Role.TRAINEE)
                .isActive(true)
                .build();

        Trainee trainee = Trainee.builder()
                .user(user)
                .dateOfBirth(LocalDate.of(1995, 5, 20))
                .address("123 Street")
                .trainers(new ArrayList<>())
                .trainings(new ArrayList<>())
                .build();

        traineeRepository.save(trainee);
    }

    @When("a training creation request is submitted:")
    public void submitTrainingCreation(DataTable dataTable) throws Exception {
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
                        "password123",
                        List.of(new SimpleGrantedAuthority("ROLE_TRAINEE"))
                );
        String token = jwtUtils.generateToken(principal);

        ResultActions resultActions = mockMvc.perform(post("/api/v1/trainings")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        context.set("lastResultActions", resultActions);
    }

    @Then("the training should exist in the database with name {string}")
    public void trainingShouldExistInDatabase(String name) {
        boolean exists = trainingRepository.findAll().stream()
                .anyMatch(t -> t.getTrainingName().equals(name));
        assertThat(exists).isTrue();
    }

    @Then("an ADD workload message should be dispatched to the trainer workload producer")
    public void workloadMessageDispatched() {
        verify(trainerWorkloadProducer).sendWorkloadUpdate(any(TrainerWorkloadRequest.class));
    }

    @Then("no workload message should be dispatched")
    public void noWorkloadMessageDispatched() {
        verify(trainerWorkloadProducer, never()).sendWorkloadUpdate(any());
    }
}