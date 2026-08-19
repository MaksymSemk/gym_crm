package com.example.gym_crm.training_type;

import com.example.gym_crm.training_type.Dto.response.TrainingTypeResponse;
import java.util.List;

public interface TrainingTypeService {
    List<TrainingTypeResponse> getAllTrainingTypes();
}