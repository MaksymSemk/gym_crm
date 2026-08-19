package com.example.gym_crm.trainee.Dto;

import com.example.gym_crm.authentication.AuthData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class TraineeTrainingsSearchDto extends AuthData {
    private LocalDate fromDate;
    private LocalDate toDate;
    private String trainerName;
    private String trainingType;
}