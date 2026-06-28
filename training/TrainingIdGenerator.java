package com.example.gym_crm.training;

import com.example.gym_crm.common.id_generator.IdGenerator;

public class TrainingIdGenerator implements IdGenerator<TrainingId> {

    @Override
    public TrainingId generateNewId(Object entity) {
        if (!(entity instanceof Training training)) {
            throw new IllegalArgumentException("Entity must be an instance of Training");
        }

        var trainingId = training.getId();
        if (trainingId.traineeId() == null ||
                trainingId.trainerId() == null ||
                trainingId.trainingDate() == null) {

            throw new IllegalArgumentException(
                    "Cannot generate TrainingId: traineeId, trainerId, and trainingDate must not be null."
            );
        }

        return trainingId;
    }
}