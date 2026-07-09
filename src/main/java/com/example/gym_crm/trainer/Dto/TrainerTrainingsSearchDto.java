package com.example.gym_crm.trainer.Dto;

import com.example.gym_crm.authentification.AuthData;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class TrainerTrainingsSearchDto extends AuthData {
    private LocalDate fromDate;
    private LocalDate toDate;
    private String traineeName;

}