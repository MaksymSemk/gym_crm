package com.example.gym_crm.trainer;

import com.example.gym_crm.common.exception.EntityDoesNotExistException;
import com.example.gym_crm.common.user.UserUtils;
import com.example.gym_crm.trainer.Dto.TrainerCreateDto;
import com.example.gym_crm.trainer.Dto.TrainerUpdateDto;
import com.example.gym_crm.training.Training;
import com.example.gym_crm.training.TrainingId;
import com.example.gym_crm.training.TrainingRepository;
import com.example.gym_crm.training_type.TrainingType;
import com.example.gym_crm.training_type.TrainingTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class TrainerServiceImpl implements TrainerService {

    @Autowired
    private TrainerRepository trainerRepository;

    private UserUtils userUtils;
    private TrainingTypeRepository trainingTypeRepository;

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    public void setUserUtils(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    @Autowired
    public void setTrainingTypeRepository(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Override
    public Trainer getTrainerByID(UUID uuid) {
        return trainerRepository.findById(uuid).orElseThrow(
                ()-> new EntityDoesNotExistException("There is no trainer with id " + uuid)
        );
    }

    @Override
    public Trainer createTrainer(TrainerCreateDto trainerCreateDto) {

        String username = userUtils.createUsername(trainerCreateDto.firstName(),  trainerCreateDto.lastName());
        String password= userUtils.generatePassword();

        if (!isAllSpecializationsExist(trainerCreateDto.specialization())){
            throw new EntityDoesNotExistException("Specialization does not exist");
        }

        Trainer newTrainer = new Trainer(
                trainerCreateDto.firstName(),
                trainerCreateDto.lastName(),
                username,
                password,
                trainerCreateDto.isActive(),
                trainerCreateDto.specialization()
        );

        return trainerRepository.save(newTrainer);
    }

    @Override
    public Trainer updateTrainer(TrainerUpdateDto trainerUpdateDto) {
        var trainer = trainerRepository.findById(trainerUpdateDto.userId()).orElseThrow(
                ()-> new EntityDoesNotExistException("There is no trainer with id " + trainerUpdateDto.userId())
        );

        boolean updatedIdentity = trainer.updateIdentity(trainerUpdateDto);
        if (updatedIdentity) {
            trainer.setUsername(userUtils.createUsername(trainer.getFirstName(), trainer.getLastName()));
        }

        if(trainerUpdateDto.specialization() != null){
            if (!isAllSpecializationsExist(trainerUpdateDto.specialization())){
                throw new EntityDoesNotExistException("Specialization does not exist");
            }
            trainer.setSpecialization(trainerUpdateDto.specialization());
        }
        if(trainerUpdateDto.isActive()!=null){
            trainer.setIsActive(trainerUpdateDto.isActive());
        }

        if(trainerUpdateDto.trainingIds() != null){
            Set<Training> updatedTrainings = validateTrainings(trainerUpdateDto.trainingIds(), trainer);
            trainer.setTrainings(updatedTrainings);
        }

        return trainerRepository.update(trainer);
    }



    private Set<Training> validateTrainings(Set<TrainingId> trainingIds, Trainer trainer) {
        Set<Training> updatedTrainings = new HashSet<>();
        for(var trainingId: trainingIds) {
            var training = trainingRepository.findById(trainingId).orElseThrow(
                    ()-> new EntityDoesNotExistException("There is no training with id " + trainingId)
            );
            if(!trainingId.trainerId().equals(trainer.getUserId())){
                throw new TrainingDoesNotBelongToTrainerException("Training with id " + trainingId + " does not belong to trainer with id " + trainer.getUserId());
            }

            updatedTrainings.add(training);
        }
        return updatedTrainings;
    }

    private boolean isAllSpecializationsExist(Set<TrainingType> specialization) {
        for (TrainingType trainingType : specialization) {
            if (!trainingTypeRepository.existsById(trainingType.getId())){
                return false;
            }
        }
        return true;
    }

}
