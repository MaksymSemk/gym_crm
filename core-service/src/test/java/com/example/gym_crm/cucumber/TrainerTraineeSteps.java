package com.example.gym_crm.cucumber;

import com.example.gym_crm.common.security.jwt.JwtUtils;
import com.example.gym_crm.common.user.Role;
import com.example.gym_crm.common.user.User;
import com.example.gym_crm.common.user.UserRepository;
import com.example.gym_crm.trainee.Dto.TraineeCreateDto;
import com.example.gym_crm.trainee.Trainee;
import com.example.gym_crm.trainee.repository.TraineeRepository;
import com.example.gym_crm.trainer.Dto.TrainerCreateDto;
import com.example.gym_crm.trainer.Trainer;
import com.example.gym_crm.trainer.repository.TrainerRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class TrainerTraineeSteps {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final UserRepository userRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final ScenarioContext context;

    public TrainerTraineeSteps(MockMvc mockMvc,
                               UserRepository userRepository,
                               TraineeRepository traineeRepository,
                               TrainerRepository trainerRepository,
                               TrainingTypeRepository trainingTypeRepository,
                               PasswordEncoder passwordEncoder,
                               JwtUtils jwtUtils,
                               ScenarioContext context) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.context = context;
    }

    @When("a trainee registration request is submitted:")
    public void submitTraineeRegistration(DataTable dataTable) throws Exception {
        Map<String, String> row = dataTable.asMaps().get(0);
        TraineeCreateDto dto = new TraineeCreateDto(
                row.get("firstName"),
                row.get("lastName"),
                row.get("dateOfBirth") != null ? LocalDate.parse(row.get("dateOfBirth")) : null,
                row.get("address")
        );
        ResultActions resultActions = mockMvc.perform(post("/api/v1/trainees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        context.set("lastResultActions", resultActions);
    }

    @When("a trainer registration request is submitted:")
    public void submitTrainerRegistration(DataTable dataTable) throws Exception {
        Map<String, String> row = dataTable.asMaps().get(0);
        TrainerCreateDto dto = new TrainerCreateDto(
                row.get("firstName"),
                row.get("lastName"),
                Long.parseLong(row.get("specializationId"))
        );
        ResultActions resultActions = mockMvc.perform(post("/api/v1/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        context.set("lastResultActions", resultActions);
    }

    @Then("the returned username should be {string}")
    public void returnedUsernameShouldBe(String username) throws Exception {
        ResultActions resultActions = context.get("lastResultActions", ResultActions.class);
        resultActions.andExpect(jsonPath("$.username").value(username));
    }

    @Then("a random 10-character password should be generated")
    public void randomPasswordGenerated() throws Exception {
        ResultActions resultActions = context.get("lastResultActions", ResultActions.class);
        String content = resultActions.andReturn().getResponse().getContentAsString();
        Map<?, ?> map = objectMapper.readValue(content, Map.class);
        String pass = (String) map.get("password");
        assertThat(pass).isNotNull().hasSize(10);
    }

    @Given("a registered trainee exists with username {string} and password {string}")
    public void registeredTraineeExists(String username, String password) {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .username(username)
                .password(passwordEncoder.encode(password))
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

        // CascadeType.ALL on Trainee automatically persists the User with it
        traineeRepository.save(trainee);
    }

    @Given("an authenticated user exists with username {string} and role {string}")
    public void authenticatedUserExists(String username, String role) {
        User user = User.builder()
                .firstName("Admin")
                .lastName("User")
                .username(username)
                .password(passwordEncoder.encode("pass"))
                .role(Role.valueOf(role))
                .isActive(true)
                .build();
        userRepository.save(user);
    }

    @When("authenticated as {string} with role {string}, a GET request is sent to {string}")
    public void authenticatedGetRequest(String username, String role, String path) throws Exception {
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        username, "pass", List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );
        String token = jwtUtils.generateToken(principal);

        ResultActions resultActions = mockMvc.perform(get(path)
                .header("Authorization", "Bearer " + token));
        context.set("lastResultActions", resultActions);
    }

    @When("an unauthenticated GET request is sent to {string}")
    public void unauthenticatedGetRequest(String path) throws Exception {
        ResultActions resultActions = mockMvc.perform(get(path));
        context.set("lastResultActions", resultActions);
    }

    @Then("the profile first name should be {string}")
    public void profileFirstNameShouldBe(String firstName) throws Exception {
        ResultActions resultActions = context.get("lastResultActions", ResultActions.class);
        resultActions.andExpect(jsonPath("$.firstName").value(firstName));
    }

    @Then("the profile active status should be true")
    public void profileActiveStatusShouldBeTrue() throws Exception {
        ResultActions resultActions = context.get("lastResultActions", ResultActions.class);
        resultActions.andExpect(jsonPath("$.isActive").value(true));
    }
}