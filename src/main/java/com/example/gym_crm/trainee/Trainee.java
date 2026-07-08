package com.example.gym_crm.trainee;

import com.example.gym_crm.common.repository.EntityId;
import com.example.gym_crm.common.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "trainees")
public class Trainee implements EntityId<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String address;

//    @Column(nullable = false)
//    private Set<Training> trainings;
//
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
