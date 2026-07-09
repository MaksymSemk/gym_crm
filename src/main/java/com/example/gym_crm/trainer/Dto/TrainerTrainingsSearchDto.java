package com.example.gym_crm.trainer.Dto;

import com.example.gym_crm.authentication.AuthData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class TrainerTrainingsSearchDto extends AuthData {
    private LocalDate fromDate;
    private LocalDate toDate;
    private String traineeName;

}