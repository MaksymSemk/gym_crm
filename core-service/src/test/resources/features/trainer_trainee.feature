Feature: Trainer and Trainee Profile Operations

  Background:
    Given a clean database state
    And a training type exists with id 1 and name "Fitness"

  # Positive Scenario: Trainee Registration
  Scenario: Register a new trainee profile successfully
    When a trainee registration request is submitted:
      | firstName | lastName | dateOfBirth | address       |
      | Alice     | Brown    | 1996-04-12  | 742 Evergreen |
    Then the response status code should be 200
    And the returned username should be "Alice.Brown"
    And a random 10-character password should be generated

  # Negative Scenario: Trainee Registration Validation Error (Future Date of Birth)
  Scenario: Trainee registration fails with future date of birth
    When a trainee registration request is submitted:
      | firstName | lastName | dateOfBirth | address       |
      | Bob       | Invalid  | 2099-01-01  | 123 Future St |
    Then the response status code should be 400

  # Positive Scenario: Trainer Registration
  Scenario: Register a new trainer profile successfully
    When a trainer registration request is submitted:
      | firstName | lastName | specializationId |
      | Marcus    | Vance    | 1                |
    Then the response status code should be 201
    And the returned username should be "Marcus.Vance"
    And a random 10-character password should be generated

  # Positive Scenario: Get Trainee Profile
  Scenario: Retrieve trainee profile with valid credentials
    Given a registered trainee exists with username "john.doe" and password "pass123"
    When authenticated as "john.doe" with role "TRAINEE", a GET request is sent to "/api/v1/trainees/john.doe"
    Then the response status code should be 200
    And the profile first name should be "John"
    And the profile active status should be true

# Negative Scenario: Access without token
  Scenario: Access trainee profile without authorization token returns 401 or 403
    Given a registered trainee exists with username "john.doe" and password "pass123"
    When an unauthenticated GET request is sent to "/api/v1/trainees/john.doe"
    Then the response status code should be 403

  # Negative Scenario: Non-existent profile
  Scenario: Retrieve non-existent trainee profile returns 404
    Given an authenticated user exists with username "admin.user" and role "TRAINEE"
    When authenticated as "admin.user" with role "TRAINEE", a GET request is sent to "/api/v1/trainees/non.existent"
    Then the response status code should be 404