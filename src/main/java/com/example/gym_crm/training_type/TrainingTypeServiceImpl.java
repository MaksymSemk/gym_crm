package com.example.gym_crm.training_type;

import com.example.gym_crm.training_type.Dto.response.TrainingTypeResponse;
import com.example.gym_crm.training_type.repository.TrainingTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;

    @Transactional(readOnly = true)
    @Override
    public List<TrainingTypeResponse> getAllTrainingTypes() {
        log.debug("Fetching all training types");
        return trainingTypeRepository.findAll().stream()
                .map(type -> new TrainingTypeResponse(type.getId(), type.getName()))
                .toList();
    }
}