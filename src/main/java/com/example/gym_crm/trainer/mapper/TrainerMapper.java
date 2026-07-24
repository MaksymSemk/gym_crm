package com.example.gym_crm.trainer.mapper;

import com.example.gym_crm.trainee.Trainee;
import com.example.gym_crm.trainer.Dto.responce.TraineeInfoDto;
import com.example.gym_crm.trainer.Dto.responce.TrainerCreatedResponse;
import com.example.gym_crm.trainer.Dto.responce.TrainerGetResponse;
import com.example.gym_crm.trainer.Dto.responce.TrainerTrainingResponse;
import com.example.gym_crm.trainer.Trainer;
import com.example.gym_crm.training.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class TrainerMapper {

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "password", source = "user.password")
    public abstract TrainerCreatedResponse toTrainerCreatedResponse(Trainer trainer);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "specialization", source = "specialization.name")
    @Mapping(target = "isActive", source = "user.isActive")
    @Mapping(target = "traineesList", source = "trainees")
    public abstract TrainerGetResponse toTrainerGetResponse(Trainer trainer);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    public abstract TraineeInfoDto toTraineeInfoDto(Trainee trainee);

    public abstract List<TraineeInfoDto> toTraineeInfoDtoList(List<Trainee> trainees);

    @Mapping(target = "trainingName", source = "trainingName")
    @Mapping(target = "trainingDate", source = "trainingDate")
    @Mapping(target = "trainingType", source = "trainingType.name")
    @Mapping(target = "trainingDuration", source = "trainingDuration")
    @Mapping(target = "traineeName", source = "trainee.user.firstName")
    public abstract TrainerTrainingResponse toTrainerTrainingResponse(Training training);

    public abstract List<TrainerTrainingResponse> toTrainerTrainingResponseList(List<Training> trainings);
}