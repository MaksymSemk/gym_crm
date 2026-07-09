package com.example.gym_crm.trainer;

import com.example.gym_crm.common.exception.EntityDoesNotExistException;
import com.example.gym_crm.common.user.User;
import com.example.gym_crm.common.user.UserRepository;
import com.example.gym_crm.common.user.UserUtils;
import com.example.gym_crm.trainee.TraineeRepository;
import com.example.gym_crm.trainer.Dto.TrainerChangePasswordDto;
import com.example.gym_crm.trainer.Dto.TrainerCreateDto;
import com.example.gym_crm.trainer.Dto.TrainerTrainingsSearchDto;
import com.example.gym_crm.trainer.Dto.TrainerUpdateDto;
import com.example.gym_crm.training.Training;
import com.example.gym_crm.training.TrainingRepository;
import com.example.gym_crm.training_type.TrainingType;
import com.example.gym_crm.training_type.TrainingTypeRepository;
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
public class TrainerServiceImpl implements TrainerService {

    private TrainerRepository trainerRepository;
    private UserRepository userRepository;
    private TrainingRepository trainingRepository;
    private TrainingTypeRepository trainingTypeRepository;
    private UserUtils userUtils;

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) { this.trainerRepository = trainerRepository; }
    @Autowired
    public void setUserRepository(UserRepository userRepository) { this.userRepository = userRepository; }
    @Autowired
    public void setTrainingRepository(TrainingRepository trainingRepository) { this.trainingRepository = trainingRepository; }
    @Autowired
    public void setTrainingTypeRepository(TrainingTypeRepository trainingTypeRepository) { this.trainingTypeRepository = trainingTypeRepository; }
    @Autowired
    public void setUserUtils(UserUtils userUtils) { this.userUtils = userUtils; }

    @Transactional
    @Override
    public Trainer createTrainer(TrainerCreateDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Trainer create DTO cannot be null");
        }

        log.debug("Creating trainer profile: {} {}", dto.firstName(), dto.lastName());

        TrainingType specialization = trainingTypeRepository.findById(dto.specializationId())
                .orElseThrow(() -> new EntityDoesNotExistException("Specialization training type not found"));

        String username = userUtils.createUsername(dto.firstName(), dto.lastName());
        String password = userUtils.generatePassword();

        User newUser = User.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .username(username)
                .password(password)
                .isActive(dto.isActive())
                .build();

        Trainer newTrainer = Trainer.builder()
                .specialization(specialization)
                .user(newUser)
                .trainings(new ArrayList<>())
                .trainees(new ArrayList<>())
                .build();

        return trainerRepository.save(newTrainer);
    }

    @Override
    public Trainer getTrainerByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        log.debug("Retrieving trainer by username: {}", username);
        return trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityDoesNotExistException("Trainer not found with username: " + username));
    }

    @Transactional
    @Override
    public Trainer updateTrainer(TrainerUpdateDto dto) {
        if (dto == null || dto.getUserId() == null) {
            throw new IllegalArgumentException("Invalid update parameters");
        }

        log.debug("Updating trainer profile for ID: {}", dto.getUserId());
        Trainer trainer = trainerRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityDoesNotExistException("Trainer not found"));

        User user = trainer.getUser();
        boolean updatedIdentity = user.updateIdentity(dto);
        if (updatedIdentity) {
            user.setUsername(userUtils.createUsername(user.getFirstName(), user.getLastName()));
        }

        if (dto.getIsActive() != null) {
            user.setIsActive(dto.getIsActive());
        }

        if (dto.getSpecializationId() != null) {
            TrainingType specialization = trainingTypeRepository.findById(dto.getSpecializationId())
                    .orElseThrow(() -> new EntityDoesNotExistException("Specialization not found"));
            trainer.setSpecialization(specialization);
        }

        userRepository.save(user);
        return trainerRepository.save(trainer);
    }

    @Transactional
    @Override
    public Trainer changePassword(TrainerChangePasswordDto dto) {
        if (dto == null || dto.getNewPassword() == null || dto.getNewPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }
        Trainer trainer = getTrainerByUsername(dto.getUsername());
        trainer.getUser().setPassword(dto.getNewPassword());
        userRepository.save(trainer.getUser());
        log.warn("Password changed for trainer: {}", dto.getUsername());
        return trainer;
    }

    @Transactional
    @Override
    public Trainer updateTrainerStatus(String username) {
        Trainer trainer = getTrainerByUsername(username);
        User user = trainer.getUser();
        user.setIsActive(!user.getIsActive());
        userRepository.save(user);
        log.debug("Trainer status updated for: {}", username);
        return trainer;
    }

    @Transactional
    @Override
    public List<Training> getTrainerTrainings(TrainerTrainingsSearchDto dto) {
        if (dto == null || dto.getUsername() == null) throw new IllegalArgumentException("Missing query payload");
        getTrainerByUsername(dto.getUsername());
        return trainingRepository.findTrainerTrainingsByCriteria(
                dto.getUsername(), dto.getFromDate(), dto.getToDate(), dto.getTraineeName()
        );
    }

    @Override
    public List<Trainer> getUnassignedTrainersByTraineeUsername(String traineeUsername) {
        if (traineeUsername == null || traineeUsername.isBlank()) {
            throw new IllegalArgumentException("Trainee username cannot be empty");
        }
        return trainerRepository.findTrainersNotAssignedToTrainee(traineeUsername);
    }

    @Override
    public Trainer getTrainerByID(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("Trainer id cannot be null");
        }
        log.debug("Retrieving trainer with ID: {}", uuid);
        return trainerRepository.findById(uuid)
                .orElseThrow(() -> new EntityDoesNotExistException("Trainer not found with id: " + uuid));
    }
}