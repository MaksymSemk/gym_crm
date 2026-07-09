package com.example.gym_crm.training_type;

import com.example.gym_crm.trainer.Trainer;
import com.example.gym_crm.training.Training;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "training_types")
public class TrainingType{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "specialization")
    private List<Trainer> trainers;

    @OneToMany(mappedBy = "trainingType")
    private List<Training> trainings;
}
