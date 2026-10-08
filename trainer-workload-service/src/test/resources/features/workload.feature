Feature: Trainer Workload Calculation and Management

  Background:
    Given a clean MongoDB workload collection

  # Positive Scenario: ADD Action Creates New Document
  Scenario: ADD action creates new workload record with year and month summaries
    When a workload request is submitted with service credentials:
      | trainerUsername | trainerFirstName | trainerLastName | isActive | trainingDate | trainingDuration | actionType |
      | alex.turner     | Alex             | Turner          | true     | 2026-08-15   | 60               | ADD        |
    Then the workload response status code should be 200
    And a workload document should exist for trainer "alex.turner"
    And the workload duration for year 2026 month 8 should be 60

  # Positive Scenario: Consecutive ADD Actions Accumulate Minutes
  Scenario: Successive ADD actions increment monthly training duration
    Given an existing workload for trainer "alex.turner" in year 2026 month 8 with 60 minutes
    When a workload request is submitted with service credentials:
      | trainerUsername | trainerFirstName | trainerLastName | isActive | trainingDate | trainingDuration | actionType |
      | alex.turner     | Alex             | Turner          | true     | 2026-08-20   | 45               | ADD        |
    Then the workload response status code should be 200
    And the workload duration for year 2026 month 8 should be 105

  # Positive Scenario: DELETE Action Decrements Minutes
  Scenario: DELETE action decrements monthly training duration
    Given an existing workload for trainer "alex.turner" in year 2026 month 8 with 90 minutes
    When a workload request is submitted with service credentials:
      | trainerUsername | trainerFirstName | trainerLastName | isActive | trainingDate | trainingDuration | actionType |
      | alex.turner     | Alex             | Turner          | true     | 2026-08-20   | 30               | DELETE     |
    Then the workload response status code should be 200
    And the workload duration for year 2026 month 8 should be 60

  # Negative / Boundary Scenario: DELETE Clamps to Zero
  Scenario: DELETE action exceeding current duration clamps monthly duration to 0
    Given an existing workload for trainer "alex.turner" in year 2026 month 8 with 30 minutes
    When a workload request is submitted with service credentials:
      | trainerUsername | trainerFirstName | trainerLastName | isActive | trainingDate | trainingDuration | actionType |
      | alex.turner     | Alex             | Turner          | true     | 2026-08-20   | 50               | DELETE     |
    Then the workload response status code should be 200
    And the workload duration for year 2026 month 8 should be 0

  # Positive Scenario: GET Trainer Workload
  Scenario: Retrieve trainer workload summary by username
    Given an existing workload for trainer "alex.turner" in year 2026 month 8 with 120 minutes
    When an authenticated GET request is sent to "/api/v1/workload/alex.turner"
    Then the workload response status code should be 200
    And the response trainer username should be "alex.turner"
    And the response total years should contain year 2026 with month 8 duration 120

  # Negative Scenario: Invalid Payload Validation
  Scenario: Workload submission fails when mandatory fields are missing or invalid
    When a workload request is submitted with service credentials:
      | trainerUsername | trainerFirstName | trainerLastName | isActive | trainingDate | trainingDuration | actionType |
      |                 | Alex             | Turner          | true     | 2026-08-15   | 0                | ADD        |
    Then the workload response status code should be 400

  # Negative Scenario: Unauthenticated Request
  Scenario: Workload request without authorization token returns 401 or 403
    When an unauthenticated POST request is sent to "/api/v1/workload" with valid payload
    Then the workload response status code should be 403

  # Negative Scenario: Querying Non-Existent Trainer
  Scenario: Retrieving non-existent trainer workload returns 400
    When an authenticated GET request is sent to "/api/v1/workload/unknown.trainer"
    Then the workload response status code should be 400