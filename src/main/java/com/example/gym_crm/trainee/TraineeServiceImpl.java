package com.example.gym_crm.trainee;

import com.example.gym_crm.common.exception.EntityDoesNotExistException;
import com.example.gym_crm.common.user.User;
import com.example.gym_crm.common.user.UserRepository;
import com.example.gym_crm.common.user.UserUtils;
import com.example.gym_crm.trainee.Dto.TraineeCreateDto;
import com.example.gym_crm.trainee.Dto.TraineeUpdateDto;
import com.example.gym_crm.trainer.Trainer;
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

    private UserUtils userUtils;

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

        userRepository.save(user);
        return traineeRepository.save(trainee);
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
    public Trainee changePassword(String username, String newPassword) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("New password cannot be null or empty");
        }

        log.debug("Changing password for trainee with username: {}", username);
        Trainee trainee = getTraineeByUsername(username);
        User user = trainee.getUser();
        user.setPassword(newPassword);
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
    public Trainee updateTraineeTrainers(String username, List<Trainer> trainers) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (trainers == null) {
            throw new IllegalArgumentException("Trainers list cannot be null");
        }

        log.debug("Updating trainers list for trainee with username: {}", username);
        Trainee trainee = getTraineeByUsername(username);
        trainee.setTrainers(trainers);
        Trainee updatedTrainee = traineeRepository.save(trainee);
        log.debug("Updated trainee {} with {} trainers", username, trainers.size());
        return updatedTrainee;
    }

}
