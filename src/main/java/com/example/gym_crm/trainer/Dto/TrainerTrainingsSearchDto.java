package com.example.gym_crm.trainer.Dto;

import com.example.gym_crm.authentification.AuthData;
import lombok.Getter;
import java.time.LocalDate;

@Getter
public class TrainerTrainingsSearchDto extends AuthData {
    private final LocalDate fromDate;
    private final LocalDate toDate;
    private final String traineeName;

    public TrainerTrainingsSearchDto(String username, String password, LocalDate fromDate, LocalDate toDate, String traineeName) {
        super(username, password);
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.traineeName = traineeName;
    }
}