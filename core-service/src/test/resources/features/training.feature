Feature: Training Session Creation and Workload Integration

  Background:
    Given a clean database state
    And a training type exists with id 1 and name "Fitness"
    And a registered trainer exists:
      | firstName | lastName | username     | specializationId |
      | Alex      | Turner   | Alex.Turner  | 1                |
    And a registered trainee exists:
      | firstName | lastName | username     |
      | Jane      | Doe      | Jane.Doe     |

  # Positive Scenario: Add Training & Verify Workload Event
  Scenario: Schedule a new training session successfully
    When a training creation request is submitted:
      | traineeUsername | trainerUsername | trainingName     | trainingDate | trainingDuration |
      | Jane.Doe        | Alex.Turner     | Morning Blast    | 2026-10-15   | 45               |
    Then the response status code should be 200
    And the training should exist in the database with name "Morning Blast"
    And an ADD workload message should be dispatched to the trainer workload producer

  # Negative Scenario: Training Creation with Missing Trainer
  Scenario: Schedule a training session fails when trainer does not exist
    When a training creation request is submitted:
      | traineeUsername | trainerUsername | trainingName     | trainingDate | trainingDuration |
      | Jane.Doe        | Unknown.Trainer | Morning Blast    | 2026-10-15   | 45               |
    Then the response status code should be 404
    And no workload message should be dispatched

  # Negative Scenario: Invalid payload format (zero or negative duration)
  Scenario: Schedule a training session fails when duration is invalid
    When a training creation request is submitted:
      | traineeUsername | trainerUsername | trainingName  | trainingDate | trainingDuration |
      | Jane.Doe        | Alex.Turner     | Morning Blast | 2026-10-15   | 0                |
    Then the response status code should be 400