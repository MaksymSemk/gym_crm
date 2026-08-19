package com.example.gym_crm.training;

import com.example.gym_crm.common.exception.EntityDoesNotExistException;
import com.example.gym_crm.trainee.Trainee;
import com.example.gym_crm.trainee.repository.TraineeRepository;
import com.example.gym_crm.trainer.Trainer;
import com.example.gym_crm.trainer.repository.TrainerRepository;
import com.example.gym_crm.training.Dto.TrainingCreateDto;
import com.example.gym_crm.training.repository.TrainingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    @Transactional
    @Override
    public Training createTraining(TrainingCreateDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Training create DTO cannot be null");
        }
        log.debug("Processing creation for training name: {}", dto.trainingName());

        Trainee trainee = traineeRepository.findByUserUsername(dto.traineeUsername())
                .orElseThrow(() -> new EntityDoesNotExistException("Trainee not found with username: " + dto.traineeUsername()));

        Trainer trainer = trainerRepository.findByUserUsername(dto.trainerUsername())
                .orElseThrow(() -> new EntityDoesNotExistException("Trainer not found with username: " + dto.trainerUsername()));

        Training newTraining = Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingType(trainer.getSpecialization())
                .trainingName(dto.trainingName())
                .trainingDate(dto.trainingDate())
                .trainingDuration(dto.trainingDuration())
                .build();

        Training savedTraining = trainingRepository.save(newTraining);
        log.info("Successfully created training session with ID: {}", savedTraining.getId());
        return savedTraining;
    }

    @Transactional
    @Override
    public Training getTraining(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Training id cannot be null");
        }
        log.debug("Retrieving training session with ID: {}", id);
        return trainingRepository.findById(id)
                .orElseThrow(() -> new EntityDoesNotExistException("Training session not found with id: " + id));
    }
}