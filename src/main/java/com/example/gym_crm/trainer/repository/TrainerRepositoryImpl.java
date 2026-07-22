//package com.example.gym_crm.trainer.repository;
//
//import com.example.gym_crm.common.repository.AbstractCustomRepository;
//import com.example.gym_crm.trainer.Trainer;
//import jakarta.persistence.NoResultException;
//import jakarta.persistence.TypedQuery;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@Repository
//public class TrainerRepositoryImpl extends AbstractCustomRepository<Trainer, UUID> implements TrainerRepository {
//
//    public TrainerRepositoryImpl() {
//        super(Trainer.class);
//    }
//
//    @Override
//    public Optional<Trainer> findByUserUsername(String username) {
//        if (username == null || username.isBlank()) {
//            return Optional.empty();
//        }
//
//        TypedQuery<Trainer> query = entityManager.createQuery(
//                "SELECT t FROM Trainer t WHERE t.user.username = :username", Trainer.class);
//        query.setParameter("username", username);
//
//        try {
//            return Optional.of(query.getSingleResult());
//        } catch (NoResultException e) {
//            return Optional.empty();
//        }
//    }
//
//    @Override
//    public List<Trainer> findTrainersNotAssignedToTrainee(String username) {
//        if (username == null || username.isBlank()) {
//            return List.of();
//        }
//
//        TypedQuery<Trainer> query = entityManager.createQuery(
//                "SELECT t FROM Trainer t WHERE t.id NOT IN (" +
//                        "  SELECT tr.id FROM Trainee tn JOIN tn.trainers tr WHERE tn.user.username = :username" +
//                        ")", Trainer.class);
//        query.setParameter("username", username);
//
//        return query.getResultList();
//    }
//}