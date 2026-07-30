package com.example.gym_crm.training.repository;

import com.example.gym_crm.common.repository.AbstractCustomRepository;
import com.example.gym_crm.training.Training;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class TrainingRepositoryImpl extends AbstractCustomRepository<Training, UUID> implements TrainingRepository {

    public TrainingRepositoryImpl() {
        super(Training.class);
    }

    @Override
    public List<Training> findTrainerTrainingsByCriteria(
            String username, LocalDate fromDate, LocalDate toDate, String traineeName) {

        StringBuilder jpql = new StringBuilder("SELECT t FROM Training t WHERE t.trainer.user.username = :username");
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);

        if (fromDate != null) {
            jpql.append(" AND t.trainingDate >= :fromDate");
            params.put("fromDate", fromDate);
        }
        if (toDate != null) {
            jpql.append(" AND t.trainingDate <= :toDate");
            params.put("toDate", toDate);
        }
        if (traineeName != null && !traineeName.isBlank()) {
            jpql.append(" AND t.trainee.user.username = :traineeName");
            params.put("traineeName", traineeName);
        }

        TypedQuery<Training> query = entityManager.createQuery(jpql.toString(), Training.class);
        params.forEach(query::setParameter);

        return query.getResultList();
    }

    @Override
    public List<Training> findTraineeTrainingsByCriteria(
            String username, LocalDate fromDate, LocalDate toDate, String trainerName, String trainingType) {

        StringBuilder jpql = new StringBuilder("SELECT t FROM Training t WHERE t.trainee.user.username = :username");
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);

        if (fromDate != null) {
            jpql.append(" AND t.trainingDate >= :fromDate");
            params.put("fromDate", fromDate);
        }
        if (toDate != null) {
            jpql.append(" AND t.trainingDate <= :toDate");
            params.put("toDate", toDate);
        }
        if (trainerName != null && !trainerName.isBlank()) {
            jpql.append(" AND t.trainer.user.username = :trainerName");
            params.put("trainerName", trainerName);
        }
        if (trainingType != null && !trainingType.isBlank()) {
            jpql.append(" AND t.trainingType.name = :trainingType");
            params.put("trainingType", trainingType);
        }

        TypedQuery<Training> query = entityManager.createQuery(jpql.toString(), Training.class);
        params.forEach(query::setParameter);

        return query.getResultList();
    }
}