package com.example.trainerworkloadservice.workload.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YearSummary {

    @Field("year")
    private int year;

    @Builder.Default
    @Field("months")
    private List<MonthSummary> months = new ArrayList<>();
}
