//package com.example.gym_crm.repository;
//
//import com.example.gym_crm.common.user.User;
//import com.example.gym_crm.trainee.Trainee;
//import com.example.gym_crm.trainee.repository.TraineeRepository;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@Transactional
//class TraineeRepositoryTest extends BaseRepositoryTest {
//
//    @Autowired private TraineeRepository traineeRepository;
//
//    @Test
//    @DisplayName("findByUserUsername: Map nested account reference parameters to fetch trainee entity successfully")
//    void findByUserUsername_ReturnsTrainee() {
//        User user = User.builder().firstName("John").lastName("Shevchenko").username("John.Shevchenko").password("p").isActive(true).build();
//        Trainee trainee = Trainee.builder().address("Kyiv").user(user).build();
//        traineeRepository.save(trainee);
//
//        Optional<Trainee> found = traineeRepository.findByUserUsername("John.Shevchenko");
//        assertTrue(found.isPresent());
//        assertEquals("Kyiv", found.get().getAddress());
//    }
//
//    @Test
//    @DisplayName("findByUserUsername: Return empty execution mapping container safely for absent profiles or illegal states")
//    void findByUserUsername_InvalidInputs_ReturnsEmpty() {
//        assertTrue(traineeRepository.findByUserUsername(null).isEmpty());
//        assertTrue(traineeRepository.findByUserUsername("").isEmpty());
//        assertTrue(traineeRepository.findByUserUsername("ghost").isEmpty());
//    }
//}