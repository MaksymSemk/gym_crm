package com.example.trainerworkloadservice.workload.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(
        name = "trainer_name_idx",
        def = "{'trainerFirstName': 1, 'trainerLastName': 1}"
)
public class TrainerWorkload {

    @Id
    @Field("trainerUsername")
    private String trainerUsername;

    @Field("trainerFirstName")
    private String trainerFirstName;

    @Field("trainerLastName")
    private String trainerLastName;

    @Field("trainerStatus")
    private Boolean trainerStatus;

    @Builder.Default
    @Field("years")
    private List<YearSummary> years = new ArrayList<>();
}