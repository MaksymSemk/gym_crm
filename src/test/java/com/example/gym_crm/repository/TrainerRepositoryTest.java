//package com.example.gym_crm.repository;
//
//import com.example.gym_crm.common.user.User;
//import com.example.gym_crm.trainee.Trainee;
//import com.example.gym_crm.trainee.repository.TraineeRepository;
//import com.example.gym_crm.trainer.Trainer;
//import com.example.gym_crm.trainer.repository.TrainerRepository;
//import com.example.gym_crm.training_type.TrainingType;
//import com.example.gym_crm.training_type.repository.TrainingTypeRepository;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@Transactional
//class TrainerRepositoryTest extends BaseRepositoryTest {
//
//    @Autowired private TrainerRepository trainerRepository;
//    @Autowired private TraineeRepository traineeRepository;
//    @Autowired private TrainingTypeRepository trainingTypeRepository;
//
//    @Test
//    @DisplayName("findByUserUsername: Return populated optional container when user account tracking match is active")
//    void findByUsername_ReturnsTrainer() {
//        TrainingType spec = new TrainingType();
//        spec.setName("Cardio");
//        trainingTypeRepository.save(spec);
//
//        User user = User.builder().firstName("Alex").lastName("Turner").username("Alex.Turner").password("p").isActive(true).build();
//        Trainer trainer = Trainer.builder().specialization(spec).user(user).build();
//        trainerRepository.save(trainer);
//
//        Optional<Trainer> found = trainerRepository.findByUserUsername("Alex.Turner");
//        assertTrue(found.isPresent());
//        assertEquals("Alex.Turner", found.get().getUser().getUsername());
//    }
//
//    @Test
//    @DisplayName("findByUserUsername: Safeguard edge behaviors and return empty container on boundary arguments")
//    void findByUsername_BoundaryArguments_ReturnsEmpty() {
//        assertTrue(trainerRepository.findByUserUsername(null).isEmpty());
//        assertTrue(trainerRepository.findByUserUsername("   ").isEmpty());
//        assertTrue(trainerRepository.findByUserUsername("non-existent-username").isEmpty());
//    }
//
//    @Test
//    @DisplayName("findTrainersNotAssignedToTrainee: Extract coaches that are completely unlinked to specific target client")
//    void findTrainersNotAssignedToTrainee_Success() {
//        TrainingType spec = new TrainingType();
//        spec.setName("Fitness");
//        trainingTypeRepository.save(spec);
//
//        User u1 = User.builder().firstName("Assigned").lastName("Coach").username("coach.assigned").password("p").isActive(true).build();
//        Trainer assigned = Trainer.builder().specialization(spec).user(u1).trainees(new ArrayList<>()).build();
//        trainerRepository.save(assigned);
//
//        User u2 = User.builder().firstName("Free").lastName("Coach").username("coach.free").password("p").isActive(true).build();
//        Trainer free = Trainer.builder().specialization(spec).user(u2).trainees(new ArrayList<>()).build();
//        trainerRepository.save(free);
//
//        User ut = User.builder().firstName("Client").lastName("One").username("client.one").password("p").isActive(true).build();
//        Trainee trainee = Trainee.builder().user(ut).trainers(new ArrayList<>(List.of(assigned))).build();
//        traineeRepository.save(trainee);
//
//        List<Trainer> nonAssignedList = trainerRepository.findTrainersNotAssignedToTrainee("client.one");
//        assertEquals(1, nonAssignedList.size());
//        assertEquals("coach.free", nonAssignedList.get(0).getUser().getUsername());
//    }
//
//    @Test
//    @DisplayName("findTrainersNotAssignedToTrainee: Return empty tracking sequence on null or blank parameters")
//    void findTrainersNotAssignedToTrainee_BoundaryInputs() {
//        assertTrue(trainerRepository.findTrainersNotAssignedToTrainee(null).isEmpty());
//        assertTrue(trainerRepository.findTrainersNotAssignedToTrainee("  ").isEmpty());
//    }
//}