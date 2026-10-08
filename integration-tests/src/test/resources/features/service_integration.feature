Feature: Microservices End-to-End Integration via Kafka

  Background:
    Given a clean state across all services
    And a training type exists with id 1 and name "CrossFit"
    And a registered trainer exists in gym CRM:
      | firstName | lastName | username     | specializationId |
      | Marcus    | Vance    | Marcus.Vance | 1                |
    And a registered trainee exists in gym CRM:
      | firstName | lastName | username   |
      | Alice     | Brown    | Alice.Brown|

  # Positive Scenario: Complete End-to-End Event Flow
  Scenario: Scheduling a training in CRM updates trainer workload summary via Kafka
    When a training session is created via gym CRM:
      | traineeUsername | trainerUsername | trainingName  | trainingDate | trainingDuration |
      | Alice.Brown     | Marcus.Vance    | Intense WOD   | 2026-11-20   | 90               |
    Then the CRM response status code should be 200
    And within 10 seconds MongoDB should record a workload duration of 90 for trainer "Marcus.Vance" in year 2026 month 11

  # Negative Scenario: Malformed Payload Routing to Dead Letter Queue (DLQ)
  Scenario: Malformed event published to Kafka is routed to DLT without updating MongoDB
    When a corrupted workload message with invalid JSON payload is published to topic "trainer-workload-topic"
    Then within 10 seconds the message should arrive in dead letter topic "trainer-workload-topic.DLT"
    And no workload records should exist in MongoDB for trainer "Unknown.Trainer"