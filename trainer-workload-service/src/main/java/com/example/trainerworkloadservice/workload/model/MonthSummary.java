package com.example.trainerworkloadservice.workload.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthSummary {

    @Field("monthNumber")
    private int monthNumber;

    @Field("trainingSummaryDuration")
    private int trainingSummaryDuration;
}