package com.example.gym_crm.trainee.Dto;

import com.example.gym_crm.authentification.AuthData;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class TraineeTrainingsSearchDto extends AuthData {
    private LocalDate fromDate;
    private LocalDate toDate;
    private String trainerName;
    private String trainingType;
}