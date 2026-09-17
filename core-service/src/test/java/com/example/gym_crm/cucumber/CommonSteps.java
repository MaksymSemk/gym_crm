package com.example.gym_crm.cucumber;

import com.example.gym_crm.common.security.jwt.JwtUtils;
import com.example.gym_crm.training_type.TrainingType;
import com.example.gym_crm.training_type.repository.TrainingTypeRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommonSteps {

    private final JdbcTemplate jdbcTemplate;
    private final ScenarioContext context;
    private final TrainingTypeRepository trainingTypeRepository;
    private final JwtUtils jwtUtils;

    public CommonSteps(JdbcTemplate jdbcTemplate,
                       ScenarioContext context,
                       TrainingTypeRepository trainingTypeRepository,
                       JwtUtils jwtUtils) {
        this.jdbcTemplate = jdbcTemplate;
        this.context = context;
        this.trainingTypeRepository = trainingTypeRepository;
        this.jwtUtils = jwtUtils;
    }

    @Given("a clean database state")
    public void cleanDatabaseState() {
        jdbcTemplate.execute("DELETE FROM trainings");
        jdbcTemplate.execute("DELETE FROM trainer_trainee");
        jdbcTemplate.execute("DELETE FROM trainees");
        jdbcTemplate.execute("DELETE FROM trainers");
        jdbcTemplate.execute("DELETE FROM users");
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

    @Then("the response status code should be {int}")
    public void responseStatusCodeShouldBe(int expectedStatus) throws Exception {
        ResultActions resultActions = context.get("lastResultActions", ResultActions.class);
        resultActions.andExpect(status().is(expectedStatus));
    }

    @Then("the response body should contain error message {string}")
    public void responseBodyShouldContainErrorMessage(String messageSubstring) throws Exception {
        ResultActions resultActions = context.get("lastResultActions", ResultActions.class);
        String content = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(content).contains(messageSubstring);
    }
}