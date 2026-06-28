package com.example.gym_crm.trainee;

import com.example.gym_crm.common.exception.EntityDoesNotExistException;
import com.example.gym_crm.common.user.UserUtils;
import com.example.gym_crm.trainee.Dto.TraineeCreateDto;
import com.example.gym_crm.trainee.Dto.TraineeUpdateDto;
import com.example.gym_crm.training.Training;
import com.example.gym_crm.training.TrainingId;
import com.example.gym_crm.training.TrainingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class TraineeServiceImpl implements TraineeService {

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private TrainingRepository trainingRepository;

    private UserUtils userUtils;

    @Autowired
    public void setUserUtils(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    @Override
    public Trainee createTrainee(TraineeCreateDto traineeCreateDto) {
        if (traineeCreateDto == null) {
            throw new IllegalArgumentException("Trainee create DTO cannot be null");
        }
        if (traineeCreateDto.dateOfBirth().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }
        String username = userUtils.createUsername(traineeCreateDto.firstName(),  traineeCreateDto.lastName());
        String password= userUtils.generatePassword();

        Trainee newTrainee = new Trainee(
                traineeCreateDto.firstName(),
                traineeCreateDto.lastName(),
                username,
                password,
                traineeCreateDto.isActive(),
                traineeCreateDto.dateOfBirth(),
                traineeCreateDto.address(),
                null
        );

        Trainee savedTrainee = traineeRepository.save(newTrainee);
        log.info("Successfully created trainee. ID: {}, Username: {}", savedTrainee.getId(), username);
        return savedTrainee;
    }

    @Override
    public Trainee updateTrainee(TraineeUpdateDto traineeUpdateDto) {
        if (traineeUpdateDto == null) {
            throw new IllegalArgumentException("Trainee update DTO cannot be null");
        }

        if (traineeUpdateDto.userId() == null) {
            throw new IllegalArgumentException("Trainee user ID cannot be null");
        }

        var trainee = traineeRepository.findById(traineeUpdateDto.userId()).orElseThrow(
                ()-> new EntityDoesNotExistException("There is no trainee with id " + traineeUpdateDto.userId())
        );

        boolean updatedIdentity = trainee.updateIdentity(traineeUpdateDto);
        if (updatedIdentity) {
            trainee.setUsername(userUtils.createUsername(trainee.getFirstName(), trainee.getLastName()));
        }

        if(traineeUpdateDto.isActive()!=null){
            trainee.setIsActive(traineeUpdateDto.isActive());
        }

        if (traineeUpdateDto.dateOfBirth() != null) {
            if (traineeUpdateDto.dateOfBirth().isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Date of birth cannot be in the future");
            }
            trainee.setDateOfBirth(traineeUpdateDto.dateOfBirth());
        }

        if(traineeUpdateDto.address()!=null && traineeUpdateDto.address().isEmpty()){
            trainee.setAddress(traineeUpdateDto.address());
        }

        if(traineeUpdateDto.trainingIds() !=null){
            Set<Training> updatedTrainings = validateTrainings(traineeUpdateDto.trainingIds(), trainee.getId());
            trainee.setTrainings(updatedTrainings);
        }

        Trainee updatedTrainee = traineeRepository.update(trainee);
        log.info("Successfully updated trainee ID: {}", updatedTrainee.getId());
        return updatedTrainee;
    }

    private Set<Training> validateTrainings(Set<TrainingId> trainingIds, UUID traineeId) {
        Set<Training> updatedTrainings = new HashSet<>();
        for(var trainingId: trainingIds) {
            var training = trainingRepository.findById(trainingId).orElseThrow(
                    ()-> new EntityDoesNotExistException("There is no training with id " + trainingId)
            );
            if(!trainingId.traineeId().equals(traineeId)){
                throw new TrainingDoesNotBelongToTraineeException("Training with id " + trainingId + " does not belong to trainee with id " + traineeId);
            }
            updatedTrainings.add(training);
        }
        return updatedTrainings;
    }

    @Override
    public void deleteTrainee(UUID id) {
        if (id == null){
            throw new IllegalArgumentException("Trainee id cannot be null");
        }

        traineeRepository.delete(id);
        log.info("Successfully deleted trainee ID: {}", id);
    }

    @Override
    public Trainee getTraineeById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Trainee id cannot be null");
        }
        return traineeRepository.findById(id).orElseThrow(
                ()-> new EntityDoesNotExistException("There is no trainee with id " + id)
        );
    }

}
