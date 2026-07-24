package com.example.gym_crm.trainee.mapper;

import com.example.gym_crm.trainee.Dto.responce.TraineeCreatedResponse;
import com.example.gym_crm.trainee.Dto.responce.TraineeGetResponse;
import com.example.gym_crm.trainee.Dto.responce.TrainerGetResponse;
import com.example.gym_crm.trainee.Dto.responce.TraineeTrainingResponse;
import com.example.gym_crm.trainee.Trainee;
import com.example.gym_crm.trainer.Trainer;
import com.example.gym_crm.training.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class TraineeMapper {

    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "isActive", source = "user.isActive")
    @Mapping(target = "trainersList", source = "trainers")
    public abstract TraineeGetResponse toTraineeGetResponse(Trainee trainee);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "specializationId", source = "specialization.id")
    public abstract TrainerGetResponse toTrainerGetResponse(Trainer trainer);

    public abstract List<TrainerGetResponse> toTrainerGetResponseList(List<Trainer> trainers);

    @Mapping(target = "trainingName", source = "trainingName")
    @Mapping(target = "trainingDate", source = "trainingDate")
    @Mapping(target = "trainingType", source = "trainingType.name")
    @Mapping(target = "trainingDuration", source = "trainingDuration")
    @Mapping(target = "trainerName", source = "trainer.user.firstName")
    public abstract TraineeTrainingResponse toTraineeTrainingResponse(Training training);

    public abstract List<TraineeTrainingResponse> toTraineeTrainingResponseList(List<Training> trainings);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "password", source = "user.password")
    public abstract TraineeCreatedResponse toTraineeCreatedResponse(Trainee trainee);

}