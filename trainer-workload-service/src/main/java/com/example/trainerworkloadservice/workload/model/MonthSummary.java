package com.example.trainerworkloadservice.workload.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthSummary {
    private int monthNumber;
    private int trainingSummaryDuration;
}