package com.example.trainerworkloadservice.cucumber;

import com.example.trainerworkloadservice.workload.dto.ActionType;
import com.example.trainerworkloadservice.workload.dto.TrainerWorkloadRequestDto;
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
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class WorkloadSteps {

    private final MockMvc mockMvc;
    private final TrainerWorkloadRepository repository;
    private final ScenarioContext context;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final SecretKey secretKey;

    public WorkloadSteps(MockMvc mockMvc,
                         TrainerWorkloadRepository repository,
                         ScenarioContext context,
                         @Value("${jwt.secret}") String jwtSecret) {
        this.mockMvc = mockMvc;
        this.repository = repository;
        this.context = context;
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    private String generateServiceToken() {
        return Jwts.builder()
                .subject("core-service")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(secretKey)
                .compact();
    }

    @Given("an existing workload for trainer {string} in year {int} month {int} with {int} minutes")
    public void existingWorkload(String username, int year, int month, int duration) {
        TrainerWorkload workload = TrainerWorkload.builder()
                .trainerUsername(username)
                .trainerFirstName("Alex")
                .trainerLastName("Turner")
                .trainerStatus(true)
                .years(new ArrayList<>(List.of(
                        YearSummary.builder()
                                .year(year)
                                .months(new ArrayList<>(List.of(
                                        MonthSummary.builder()
                                                .monthNumber(month)
                                                .trainingSummaryDuration(duration)
                                                .build()
                                )))
                                .build()
                )))
                .build();
        repository.save(workload);
    }

    @When("a workload request is submitted with service credentials:")
    public void submitWorkloadRequest(DataTable dataTable) throws Exception {
        Map<String, String> row = dataTable.asMaps().get(0);

        TrainerWorkloadRequestDto dto = new TrainerWorkloadRequestDto(
                row.get("trainerUsername"),
                row.get("trainerFirstName"),
                row.get("trainerLastName"),
                Boolean.parseBoolean(row.get("isActive")),
                LocalDate.parse(row.get("trainingDate")),
                Integer.parseInt(row.get("trainingDuration")),
                ActionType.valueOf(row.get("actionType"))
        );

        String token = generateServiceToken();

        ResultActions resultActions = mockMvc.perform(post("/api/v1/workload")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        context.set("lastResultActions", resultActions);
    }

    @When("an authenticated GET request is sent to {string}")
    public void authenticatedGetRequest(String path) throws Exception {
        String token = generateServiceToken();

        ResultActions resultActions = mockMvc.perform(get(path)
                .header("Authorization", "Bearer " + token));

        context.set("lastResultActions", resultActions);
    }

    @When("an unauthenticated POST request is sent to {string} with valid payload")
    public void unauthenticatedPostRequest(String path) throws Exception {
        TrainerWorkloadRequestDto dto = new TrainerWorkloadRequestDto(
                "alex.turner", "Alex", "Turner", true,
                LocalDate.of(2026, 8, 15), 60, ActionType.ADD
        );

        ResultActions resultActions = mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        context.set("lastResultActions", resultActions);
    }

    @Then("a workload document should exist for trainer {string}")
    public void workloadDocumentShouldExist(String username) {
        assertThat(repository.findByTrainerUsername(username)).isPresent();
    }

    @Then("the workload duration for year {int} month {int} should be {int}")
    public void workloadDurationShouldBe(int year, int month, int expectedDuration) {
        TrainerWorkload workload = repository.findAll().get(0);
        YearSummary yearSummary = workload.getYears().stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElseThrow();

        MonthSummary monthSummary = yearSummary.getMonths().stream()
                .filter(m -> m.getMonthNumber() == month)
                .findFirst()
                .orElseThrow();

        assertThat(monthSummary.getTrainingSummaryDuration()).isEqualTo(expectedDuration);
    }

    @Then("the response trainer username should be {string}")
    public void responseTrainerUsernameShouldBe(String expectedUsername) throws Exception {
        ResultActions resultActions = context.get("lastResultActions", ResultActions.class);
        resultActions.andExpect(jsonPath("$.trainerUsername").value(expectedUsername));
    }

    @Then("the response total years should contain year {int} with month {int} duration {int}")
    public void responseYearsShouldContain(int year, int month, int duration) throws Exception {
        ResultActions resultActions = context.get("lastResultActions", ResultActions.class);
        resultActions.andExpect(jsonPath("$.years[?(@.year == " + year + ")].months[?(@.monthNumber == " + month + ")].trainingSummaryDuration").value(duration));
    }
}