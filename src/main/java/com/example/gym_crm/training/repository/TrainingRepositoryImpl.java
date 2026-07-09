package com.example.gym_crm.training.repository;

import com.example.gym_crm.common.repository.AbstractCustomRepository;
import com.example.gym_crm.training.Training;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public class TrainingRepositoryImpl extends AbstractCustomRepository<Training, UUID> implements TrainingRepository {

    public TrainingRepositoryImpl() {
        super(Training.class);
    }

    @Override
    public List<Training> findTrainerTrainingsByCriteria(
            String username, LocalDate fromDate, LocalDate toDate, String traineeName) {

        String jpql = "SELECT t FROM Training t WHERE t.trainer.user.username = :username " +
                "AND (:fromDate IS NULL OR t.trainingDate >= :fromDate) " +
                "AND (:toDate IS NULL OR t.trainingDate <= :toDate) " +
                "AND (:traineeName IS NULL OR t.trainee.user.username = :traineeName)";

        TypedQuery<Training> query = entityManager.createQuery(jpql, Training.class);
        query.setParameter("username", username);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        query.setParameter("traineeName", traineeName);

        return query.getResultList();
    }

    @Override
    public List<Training> findTraineeTrainingsByCriteria(
            String username, LocalDate fromDate, LocalDate toDate, String trainerName, String trainingType) {

        String jpql = "SELECT t FROM Training t WHERE t.trainee.user.username = :username " +
                "AND (:fromDate IS NULL OR t.trainingDate >= :fromDate) " +
                "AND (:toDate IS NULL OR t.trainingDate <= :toDate) " +
                "AND (:trainerName IS NULL OR t.trainer.user.username = :trainerName) " +
                "AND (:trainingType IS NULL OR t.trainingType.name = :trainingType)";

        TypedQuery<Training> query = entityManager.createQuery(jpql, Training.class);
        query.setParameter("username", username);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        query.setParameter("trainerName", trainerName);
        query.setParameter("trainingType", trainingType);

        return query.getResultList();
    }
}