package com.example.gym_crm.training;

import com.example.gym_crm.common.exception.EntityAlreadyExistsException;
import com.example.gym_crm.common.exception.EntityDoesNotExistException;
import com.example.gym_crm.trainee.TraineeRepository;
import com.example.gym_crm.trainer.TrainerRepository;
import com.example.gym_crm.trainer.TrainingDoesNotBelongToTrainerException;
import com.example.gym_crm.training.Dto.TrainingCreateDto;
import com.example.gym_crm.training.Dto.TrainingUpdateDto;
import com.example.gym_crm.training_type.TrainingType;
import com.example.gym_crm.training_type.TrainingTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class TrainingServiceImpl implements TrainingService {

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TraineeRepository traineeRepository;

    @Override
    public Training createTraining(TrainingCreateDto trainingCreateDto) {
        if (trainingCreateDto == null) {
            throw new IllegalArgumentException("Training create DTO cannot be null");
        }

        validateTrainingCreation(trainingCreateDto);

        Training newTraining = new Training(
                trainingCreateDto.trainingId(),
                trainingCreateDto.trainingName(),
                trainingCreateDto.trainingTypes(),
                trainingCreateDto.trainingDuration()
        );

        return trainingRepository.save(newTraining);
    }

    @Override
    public Training updateTraining(TrainingUpdateDto trainingUpdateDto) {
        if (trainingUpdateDto == null) {
            throw new IllegalArgumentException("Training update DTO cannot be null");
        }

        if (trainingUpdateDto.trainingId() == null) {
            throw new IllegalArgumentException("Training ID cannot be null");
        }

        var training = trainingRepository.findById(trainingUpdateDto.trainingId()).orElseThrow(
                () -> new EntityDoesNotExistException("There is no training with id " + trainingUpdateDto.trainingId())
        );

        if (trainingUpdateDto.trainingName() != null && !trainingUpdateDto.trainingName().isEmpty()) {
            training.setTrainingName(trainingUpdateDto.trainingName());
        }

        if (trainingUpdateDto.trainingTypes() != null && !trainingUpdateDto.trainingTypes().isEmpty()) {
            validateTrainingTypes(trainingUpdateDto.trainingTypes(), trainingUpdateDto.trainingId().trainerId());
            training.setTrainingTypes(trainingUpdateDto.trainingTypes());
        }

        if (trainingUpdateDto.trainingDuration() != null) {
            training.setTrainingDuration(trainingUpdateDto.trainingDuration());
        }

        return trainingRepository.update(training);
    }

    private void validateTrainingCreation(TrainingCreateDto trainingCreateDto) {
        if( trainingRepository.existsById(trainingCreateDto.trainingId())) {
            throw new EntityAlreadyExistsException("Training with id " + trainingCreateDto.trainingId() + " already exists");
        }

        if (!traineeRepository.existsById(trainingCreateDto.trainingId().traineeId())) {
            throw new EntityDoesNotExistException("Trainee with id " + trainingCreateDto.trainingId().traineeId() + " does not exist");
        }

        var trainer = trainerRepository.findById(trainingCreateDto.trainingId().trainerId()).orElseThrow(
                () -> new EntityDoesNotExistException("Trainer with id " + trainingCreateDto.trainingId().trainerId() + " does not exist")
        );

        for (TrainingType trainingType : trainingCreateDto.trainingTypes()) {
            if (!trainingTypeRepository.existsById(trainingType.getId())) {
                throw new EntityDoesNotExistException("Training type with id " + trainingType.getId() + " does not exist");
            }
            if (!trainer.getSpecialization().contains(trainingType)){
                throw new TrainingDoesNotBelongToTrainerException("Trainer with id " + trainer.getId() + " is not specialized in training type " + trainingType.getName());
            }
        }

    }

    private void validateTrainingTypes(Set<TrainingType> trainingTypes, UUID trainerId) {
        var trainer = trainerRepository.findById(trainerId).orElseThrow(
                () -> new EntityDoesNotExistException("Trainer with id " + trainerId + " does not exist")
        );

        for (TrainingType trainingType : trainingTypes) {
            if (!trainingTypeRepository.existsById(trainingType.getId())) {
                throw new EntityDoesNotExistException("Training type with id " + trainingType.getId() + " does not exist");
            }
            if (!trainer.getSpecialization().contains(trainingType)) {
                throw new TrainingDoesNotBelongToTrainerException("Trainer with id " + trainer.getId() + " is not specialized in training type " + trainingType.getName());
            }
        }
    }

    @Override
    public Training getTraining(TrainingId id) {
        if (id == null) {
            throw new IllegalArgumentException("Training ID cannot be null");
        }

        return trainingRepository.findById(id).orElseThrow(
                () -> new EntityDoesNotExistException("There is no training with id " + id)
        );
    }
}
