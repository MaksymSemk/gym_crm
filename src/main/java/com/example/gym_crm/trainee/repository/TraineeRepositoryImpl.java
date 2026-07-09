package com.example.gym_crm.trainee.repository;

import com.example.gym_crm.common.repository.AbstractCustomRepository;
import com.example.gym_crm.trainee.Trainee;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class TraineeRepositoryImpl extends AbstractCustomRepository<Trainee, UUID> implements TraineeRepository {

    public TraineeRepositoryImpl() {
        super(Trainee.class);
    }

    @Override
    public Optional<Trainee> findByUserUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }

        TypedQuery<Trainee> query = entityManager.createQuery(
                "SELECT t FROM Trainee t WHERE t.user.username = :username", Trainee.class);
        query.setParameter("username", username);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}