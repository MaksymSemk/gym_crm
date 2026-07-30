package com.example.gym_crm.training;

import com.example.gym_crm.common.exception.EntityDoesNotExistException;
import com.example.gym_crm.trainee.Trainee;
import com.example.gym_crm.trainee.repository.TraineeRepository;
import com.example.gym_crm.trainer.Trainer;
import com.example.gym_crm.trainer.repository.TrainerRepository;
import com.example.gym_crm.training.Dto.TrainingCreateDto;
import com.example.gym_crm.training.repository.TrainingRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class TrainingServiceImpl implements TrainingService {

    private TrainingRepository trainingRepository;
    private TraineeRepository traineeRepository;
    private TrainerRepository trainerRepository;

    @Autowired
    public void setTrainingRepository(TrainingRepository trainingRepository) { this.trainingRepository = trainingRepository; }
    @Autowired
    public void setTraineeRepository(TraineeRepository traineeRepository) { this.traineeRepository = traineeRepository; }
    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) { this.trainerRepository = trainerRepository; }

    @Transactional
    @Override
    public Training createTraining(TrainingCreateDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Training create DTO cannot be null");
        }

        log.debug("Processing creation for training name: {}", dto.getTrainingName());

        Trainee trainee = traineeRepository.findByUserUsername(dto.getTraineeUsername())
                .orElseThrow(() -> new EntityDoesNotExistException("Trainee not found with username: " + dto.getTraineeUsername()));

        Trainer trainer = trainerRepository.findByUserUsername(dto.getTrainerUsername())
                .orElseThrow(() -> new EntityDoesNotExistException("Trainer not found with username: " + dto.getTrainerUsername()));

        Training newTraining = Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingType(trainer.getSpecialization())
                .trainingName(dto.getTrainingName())
                .trainingDate(dto.getTrainingDate())
                .trainingDuration(dto.getTrainingDuration())
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