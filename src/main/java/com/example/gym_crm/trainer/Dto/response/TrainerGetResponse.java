package com.example.gym_crm.trainer.Dto.response;

import java.util.List;

public record TrainerGetResponse(
        String username,
        String firstName,
        String lastName,
        String specialization,
        Boolean isActive,
        List<TraineeInfoDto> traineesList
) {}