package com.example.gym_crm.trainee.Dto;

import com.example.gym_crm.authentification.AuthData;
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

    public TraineeTrainingsSearchDto(String username, String password, LocalDate fromDate, LocalDate toDate, String trainerName, String trainingType) {
        super(username, password);
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.trainerName = trainerName;
        this.trainingType = trainingType;
    }
}