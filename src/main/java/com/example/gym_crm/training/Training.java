package com.example.gym_crm.training;

import com.example.gym_crm.common.repository.EntityId;
import com.example.gym_crm.training_type.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Training implements EntityId<TrainingId> {
    private TrainingId id;

    private String trainingName;
    private Set<TrainingType> trainingTypes;
    private LocalTime trainingDuration;
}


