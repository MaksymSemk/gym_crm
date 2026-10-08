package com.example.gym_crm.cucumber;

import com.example.gym_crm.authentication.dto.ChangePasswordRequestDto;
import com.example.gym_crm.common.security.jwt.JwtUtils;
import com.example.gym_crm.common.user.Role;
import com.example.gym_crm.common.user.User;
import com.example.gym_crm.common.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthSteps {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final ScenarioContext context;

    public AuthSteps(MockMvc mockMvc,
                     UserRepository userRepository,
                     PasswordEncoder passwordEncoder,
                     JwtUtils jwtUtils,
                     ScenarioContext context) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.context = context;
    }

    @Given("an existing user in the database:")
    public void anExistingUserInTheDatabase(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            User user = User.builder()
                    .firstName(row.get("firstName"))
                    .lastName(row.get("lastName"))
                    .username(row.get("username"))
                    .password(passwordEncoder.encode(row.get("password")))
                    .role(Role.valueOf(row.get("role")))
                    .isActive(Boolean.parseBoolean(row.get("isActive")))
                    .build();
            userRepository.save(user);
        }
    }

    @When("the user logs in with username {string} and password {string}")
    public void theUserLogsIn(String username, String password) throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/api/v1/auth/login")
                .param("username", username)
                .param("password", password));
        context.set("lastResultActions", resultActions);
    }

    @Then("the response should contain a valid JWT token for username {string}")
    public void responseShouldContainValidJwt(String username) throws Exception {
        ResultActions resultActions = context.get("lastResultActions", ResultActions.class);
        String token = org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.token")
                .value(resultActions.andReturn()).toString();

        String content = resultActions.andReturn().getResponse().getContentAsString();
        Map<?, ?> responseMap = objectMapper.readValue(content, Map.class);
        String jwt = (String) responseMap.get("token");

        assertThat(jwt).isNotBlank();
        assertThat(jwtUtils.validateToken(jwt)).isTrue();
        assertThat(jwtUtils.getUsernameFromToken(jwt)).isEqualTo(username);
    }

    @When("the user fails to log in {int} consecutive times with username {string} and password {string}")
    public void userFailsToLoginConsecutiveTimes(int times, String username, String password) throws Exception {
        // 5 attempts all return 401 Unauthorized, incrementing counter to 5
        for (int i = 0; i < times; i++) {
            mockMvc.perform(get("/api/v1/auth/login")
                            .param("username", username)
                            .param("password", password))
                    .andExpect(status().isUnauthorized());
        }
    }

    @When("the user attempts to log in a 6th time with username {string} and password {string}")
    public void userAttemptsToLogin6thTime(String username, String password) throws Exception {
        // 6th attempt hits the block check (attempt.count >= 5) and receives 429
        ResultActions blockedAttempt = mockMvc.perform(get("/api/v1/auth/login")
                .param("username", username)
                .param("password", password));
        context.set("lastResultActions", blockedAttempt);
    }

    @When("the user changes password from {string} to {string} for username {string}")
    public void userChangesPassword(String oldPass, String newPass, String username) throws Exception {
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto(username, oldPass, newPass);
        ResultActions resultActions = mockMvc.perform(put("/api/v1/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        context.set("lastResultActions", resultActions);
    }

    @Then("the user should be able to log in with username {string} and new password {string}")
    public void verifyUserCanLoginWithNewPassword(String username, String newPassword) throws Exception {
        mockMvc.perform(get("/api/v1/auth/login")
                        .param("username", username)
                        .param("password", newPassword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
}