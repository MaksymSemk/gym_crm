package com.example.gym_crm.trainee.Dto.responce;

import java.time.LocalDate;
import java.util.List;

public record TraineeGetResponse (
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String address,
        Boolean isActive,
        List<TrainerGetResponse> trainersList
){
}

