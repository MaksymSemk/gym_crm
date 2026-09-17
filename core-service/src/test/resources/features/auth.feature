Feature: Authentication and Security Management

  Background:
    Given a clean database state
    And an existing user in the database:
      | firstName | lastName | username | password    | role    | isActive |
      | John      | Doe      | john.doe | password123 | TRAINEE | true     |

  # Positive Scenario
  Scenario: Successful login returns 200 OK and JWT token
    When the user logs in with username "john.doe" and password "password123"
    Then the response status code should be 200
    And the response should contain a valid JWT token for username "john.doe"

  # Negative Scenario: Invalid Credentials
  Scenario: Login with incorrect password returns 401 Unauthorized
    When the user logs in with username "john.doe" and password "wrongpassword"
    Then the response status code should be 401
    And the response body should contain error message "Invalid username or password"

# Negative Scenario: Rate Limiting / Brute Force Lockout
  Scenario: Five failed login attempts block the sixth attempt with HTTP 429
    When the user fails to log in 5 consecutive times with username "john.doe" and password "bad_pass"
    And the user attempts to log in a 6th time with username "john.doe" and password "bad_pass"
    Then the response status code should be 429
    And the response body should contain error message "Too many failed login attempts"

  # Positive Scenario: Change Password
  Scenario: Successfully change user password
    When the user changes password from "password123" to "newSecret123" for username "john.doe"
    Then the response status code should be 200
    And the user should be able to log in with username "john.doe" and new password "newSecret123"

  # Negative Scenario: Change Password with wrong old password
  Scenario: Change password fails when old password does not match
    When the user changes password from "incorrectOld" to "newSecret123" for username "john.doe"
    Then the response status code should be 401
    And the response body should contain error message "Invalid username or password"