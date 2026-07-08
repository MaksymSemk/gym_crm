package com.example.gym_crm.trainer;

import com.example.gym_crm.common.repository.EntityId;
import com.example.gym_crm.common.user.User;
import com.example.gym_crm.training_type.TrainingType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "trainers")
public class Trainer implements EntityId<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "trainer_training_type",
            joinColumns = @JoinColumn(name = "trainer_id"),
            inverseJoinColumns = @JoinColumn(name = "training_type_id")
    )
    private List<TrainingType> specialization;
    //private Set<Training> trainings;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Override
    public UUID getId() {
        return user.getId();
    }

    @Override
    public void setId(UUID id) {
        this.user.setId(id);
    }
}
