package com.example.gym_crm.trainee.Dto.responce;

public record TrainerGetResponse (
    String username,
    String firstName,
    String lastName,
    Long specializationId
) {
}