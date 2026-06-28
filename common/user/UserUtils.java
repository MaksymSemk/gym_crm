package com.example.gym_crm.common.user;

import com.example.gym_crm.trainee.TraineeRepository;
import com.example.gym_crm.trainer.TrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class UserUtils {

    private TrainerRepository trainerRepository;
    private TraineeRepository traineeRepository;

    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{}|;:,.<>?";

    private static final SecureRandom random = new SecureRandom();

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Autowired
    public void setTraineeRepository(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    public String createUsername(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            throw new IllegalArgumentException("Names cannot be null for username generation");
        }

        String initialUsername = firstName.trim() + "." + lastName.trim();
        String resultUsername = initialUsername;
        int counter = 1;

        var trainers = trainerRepository.findAll().stream().map(User::getUsername).toList();
        var trainee = traineeRepository.findAll().stream().map(User::getUsername).toList();

        while (trainers.contains(resultUsername) || trainee.contains(resultUsername)) {
            resultUsername = initialUsername + counter;
            counter++;
        }

        return resultUsername;
    }

    public  String generatePassword() {
        int length = 10;
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(randomIndex));
        }

        return password.toString();
    }
}
