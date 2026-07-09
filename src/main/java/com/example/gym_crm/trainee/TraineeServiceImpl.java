package com.example.gym_crm.trainee;

import com.example.gym_crm.common.exception.EntityDoesNotExistException;
import com.example.gym_crm.common.user.User;
import com.example.gym_crm.common.user.UserRepository;
import com.example.gym_crm.common.user.UserUtils;
import com.example.gym_crm.trainee.Dto.*;
import com.example.gym_crm.trainee.repository.TraineeRepository;
import com.example.gym_crm.trainer.repository.TrainerRepository;
import com.example.gym_crm.trainer.TrainingDoesNotBelongToTrainerException;
import com.example.gym_crm.training.Training;
import com.example.gym_crm.training.repository.TrainingRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TraineeServiceImpl implements TraineeService {

    private TraineeRepository traineeRepository;

    private UserRepository userRepository;

    private TrainingRepository trainingRepository;

    private UserUtils userUtils;

    private TrainerRepository trainerRepository;

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Autowired
    public void setUserUtils(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setTraineeRepository(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Autowired
    public void setTrainingRepository(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    @Transactional
    @Override
    public Trainee createTrainee(TraineeCreateDto traineeCreateDto) {
        if (traineeCreateDto == null) {
            throw new IllegalArgumentException("Trainee create DTO cannot be null");
        }

        log.debug("Creating trainee: {} {}", traineeCreateDto.firstName(), traineeCreateDto.lastName());

        if (traineeCreateDto.dateOfBirth().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }

        String username = userUtils.createUsername(traineeCreateDto.firstName(), traineeCreateDto.lastName());
        String password = userUtils.generatePassword();

        User newUser = User.builder()
                .firstName(traineeCreateDto.firstName())
                .lastName(traineeCreateDto.lastName())
                .username(username)
                .password(password)
                .isActive(traineeCreateDto.isActive())
                .build();

        Trainee newTrainee = Trainee.builder()
                .dateOfBirth(traineeCreateDto.dateOfBirth())
                .address(traineeCreateDto.address())
                .user(newUser)
                .trainers(new ArrayList<>())
                .build();

        Trainee savedTrainee = traineeRepository.save(newTrainee);
        log.debug("Created trainee successfully with ID: {}", savedTrainee.getId());
        return savedTrainee;
    }

    @Transactional
    @Override
    public Trainee updateTrainee(TraineeUpdateDto traineeUpdateDto) {
        if (traineeUpdateDto == null) {
            throw new IllegalArgumentException("Trainee update DTO cannot be null");
        }

        log.debug("Updating trainee with ID: {}", traineeUpdateDto.getUserId());
        if (traineeUpdateDto.getUserId() == null) {
            throw new IllegalArgumentException("Trainee user ID cannot be null");
        }

        var trainee = traineeRepository.findById(traineeUpdateDto.getUserId()).orElseThrow(
                ()-> new EntityDoesNotExistException("There is no trainee with id " + traineeUpdateDto.getUserId())
        );

        List<Training> trainings = getAllTrainings(traineeUpdateDto.getTraining_ids(), trainee.getId());

        User user = trainee.getUser();
        boolean updatedIdentity = user.updateIdentity(traineeUpdateDto);
        if (updatedIdentity) {
            user.setUsername(userUtils.createUsername(user.getFirstName(), user.getLastName()));
        }

        if(traineeUpdateDto.getIsActive()!=null){
            user.setIsActive(traineeUpdateDto.getIsActive());
        }

        if (traineeUpdateDto.getDateOfBirth() != null) {
            if (traineeUpdateDto.getDateOfBirth().isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Date of birth cannot be in the future");
            }
            trainee.setDateOfBirth(traineeUpdateDto.getDateOfBirth());
        }

        if(traineeUpdateDto.getAddress()!=null && !traineeUpdateDto.getAddress().isBlank()){
            trainee.setAddress(traineeUpdateDto.getAddress());
        }

        if (trainings != null) {
            trainee.setTrainings(trainings);
        }

        userRepository.save(user);
        return traineeRepository.save(trainee);
    }

    private List<Training> getAllTrainings(List<UUID> trainingIds, UUID traineeId) {
        if (trainingIds == null) return null;
        return trainingIds.stream().map( id->{
            Training training = trainingRepository.findById(id).orElseThrow(
                () -> new EntityDoesNotExistException("There is no training with id " + id)
            );
            if (!training.getTrainee().getId().equals(traineeId)) {
                throw new TrainingDoesNotBelongToTrainerException("Training with ID " + id + " does not belong to the trainee with id " + traineeId);
            }

            return training;
        }).toList();
    }

    @Transactional
    @Override
    public void deleteTrainee(UUID id) {
        if (id == null){
            throw new IllegalArgumentException("Trainee id cannot be null");
        }
        log.warn("Attempting to delete trainee with ID: {}", id);

        Trainee trainee = traineeRepository.findById(id).orElseThrow(
                () -> new EntityDoesNotExistException("There is no trainee with id " + id)
        );

        User user = trainee.getUser();
        traineeRepository.deleteById(id);
        if (user != null) {
            userRepository.deleteById(user.getId());
        }
    }

    @Transactional
    @Override
    public Trainee getTraineeById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Trainee id cannot be null");
        }

        log.debug("Retrieving trainee with ID: {}", id);
        return traineeRepository.findById(id).orElseThrow(
                ()-> new EntityDoesNotExistException("There is no trainee with id " + id)
        );
    }

    @Transactional
    @Override
    public Trainee getTraineeByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        log.debug("Retrieving trainee by username: {}", username);
        return traineeRepository.findByUserUsername(username).orElseThrow(
                () -> new EntityDoesNotExistException("There is no trainee with username " + username)
        );
    }

    @Transactional
    @Override
    public Trainee changePassword(TraineeChangePasswordDto dto) {
        if (dto == null || dto.getNewPassword() == null || dto.getNewPassword().isBlank()) {
            throw new IllegalArgumentException("Invalid password update data");
        }
        Trainee trainee = getTraineeByUsername(dto.getUsername());
        log.debug("Changing password for trainee with username: {}", dto.getUsername());
        User user = trainee.getUser();
        user.setPassword(dto.getNewPassword());
        userRepository.save(user);
        return trainee;
    }

    @Transactional
    @Override
    public Trainee updateTraineeStatus(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        log.debug("Updating activation status for trainee with username: {}", username);
        Trainee trainee = getTraineeByUsername(username);
        User user = trainee.getUser();
        Boolean currentStatus = user.getIsActive();
        user.setIsActive(!currentStatus);
        log.debug("Trainee {} status changed from {} to {}", username, currentStatus, !currentStatus);
        userRepository.save(user);
        return trainee;
    }

    @Transactional
    @Override
    public void deleteTraineeByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        log.warn("Attempting to delete trainee with username: {}", username);
        Trainee trainee = getTraineeByUsername(username);
        UUID traineeId = trainee.getId();
        User user = trainee.getUser();

        traineeRepository.deleteById(traineeId);
        if (user != null) {
            userRepository.deleteById(user.getId());
        }
        log.warn("Trainee {} deleted successfully", username);
    }

    @Transactional
    @Override
    public Trainee updateTraineeTrainers(TraineeUpdateTrainersDto dto) {
        if (dto == null || dto.getUsername() == null || dto.getTrainerIds() == null) {
            throw new IllegalArgumentException("Invalid trainers re-assignment payload data");
        }
        var trainers = dto.getTrainerIds().stream().map(trainerId -> trainerRepository.findById(trainerId).orElseThrow(
                () -> new EntityDoesNotExistException("There is no trainer with id " + trainerId)
        )).toList();

        log.debug("Updating trainers list for trainee with username: {}", dto.getUsername());
        Trainee trainee = getTraineeByUsername(dto.getUsername());
        trainee.setTrainers(trainers);
        Trainee updatedTrainee = traineeRepository.save(trainee);
        log.debug("Updated trainee {} with {} trainers", dto.getUsername(), trainers.size());
        return updatedTrainee;
    }

    @Transactional
    @Override
    public List<Training> getTraineeTrainings(TraineeTrainingsSearchDto dto) {
        if (dto == null || dto.getUsername() == null) throw new IllegalArgumentException("Invalid filters context");
        getTraineeByUsername(dto.getUsername());
        return trainingRepository.findTraineeTrainingsByCriteria(
                dto.getUsername(), dto.getFromDate(), dto.getToDate(), dto.getTrainerName(), dto.getTrainingType()
        );
    }
}
