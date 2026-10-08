package com.example.trainerworkloadservice.cucumber;

import com.example.trainerworkloadservice.workload.repository.TrainerWorkloadRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommonWorkloadSteps {

    private final TrainerWorkloadRepository repository;
    private final ScenarioContext context;

    public CommonWorkloadSteps(TrainerWorkloadRepository repository, ScenarioContext context) {
        this.repository = repository;
        this.context = context;
    }

    @Given("a clean MongoDB workload collection")
    public void cleanMongoCollection() {
        repository.deleteAll();
    }

    @Then("the workload response status code should be {int}")
    public void workloadResponseStatusCodeShouldBe(int expectedStatus) throws Exception {
        ResultActions resultActions = context.get("lastResultActions", ResultActions.class);
        resultActions.andExpect(status().is(expectedStatus));
    }
}