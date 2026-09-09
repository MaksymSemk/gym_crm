package com.example.trainerworkloadservice.workload;

import com.example.trainerworkloadservice.workload.dto.ActionType;
import com.example.trainerworkloadservice.workload.dto.TrainerWorkloadRequestDto;
import com.example.trainerworkloadservice.workload.model.MonthSummary;
import com.example.trainerworkloadservice.workload.model.TrainerWorkload;
import com.example.trainerworkloadservice.workload.model.YearSummary;
import com.example.trainerworkloadservice.workload.repository.TrainerWorkloadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerWorkloadService {

    private final TrainerWorkloadRepository repository;

    public void processWorkload(TrainerWorkloadRequestDto request) {
        String username = request.trainerUsername();
        int reqYear = request.trainingDate().getYear();
        int reqMonth = request.trainingDate().getMonthValue();
        int duration = request.trainingDuration();
        ActionType action = request.actionType();

        log.info("[TX-START] Processing workload transaction for trainer: '{}', Action: {}, Duration: {}m, Date: {}-{}",
                username, action, duration, reqYear, reqMonth);

        log.debug("Operation [1/5]: Extracting TrainerWorkload document from MongoDB for username: '{}'", username);
        TrainerWorkload workload = repository.findByTrainerUsername(username)
                .orElseGet(() -> {
                    log.debug("Operation [1/5]: No existing record found for '{}'. Initializing new document.", username);
                    TrainerWorkload newWorkload = new TrainerWorkload();
                    newWorkload.setTrainerUsername(username);
                    newWorkload.setYears(new ArrayList<>());
                    return newWorkload;
                });

        log.debug("Operation [2/5]: Updating trainer profile data: firstName='{}', lastName='{}', status={}",
                request.trainerFirstName(), request.trainerLastName(), request.isActive());
        workload.setTrainerFirstName(request.trainerFirstName());
        workload.setTrainerLastName(request.trainerLastName());
        workload.setTrainerStatus(request.isActive());

        log.debug("Operation [3/5]: Locating YearSummary element for year: {}", reqYear);
        YearSummary yearSummary = workload.getYears().stream()
                .filter(y -> y.getYear() == reqYear)
                .findFirst()
                .orElseGet(() -> {
                    log.debug("Operation [3/5]: YearSummary not found for year {}. Creating new entry.", reqYear);
                    YearSummary y = new YearSummary(reqYear, new ArrayList<>());
                    workload.getYears().add(y);
                    return y;
                });

        log.debug("Operation [4/5]: Locating MonthSummary element for month: {}", reqMonth);
        MonthSummary monthSummary = yearSummary.getMonths().stream()
                .filter(m -> m.getMonthNumber() == reqMonth)
                .findFirst()
                .orElseGet(() -> {
                    log.debug("Operation [4/5]: MonthSummary not found for month {}. Initializing with duration 0.", reqMonth);
                    MonthSummary m = new MonthSummary(reqMonth, 0);
                    yearSummary.getMonths().add(m);
                    return m;
                });

        int currentDuration = monthSummary.getTrainingSummaryDuration();
        int newDuration = calculateDuration(currentDuration, duration, action);

        log.debug("Operation [4/5]: Duration calculation: current={}m, action={}, input={}m -> new={}m",
                currentDuration, action, duration, newDuration);
        monthSummary.setTrainingSummaryDuration(newDuration);

        log.debug("Operation [5/5]: Saving TrainerWorkload document to MongoDB collection 'trainer_workloads'");
        TrainerWorkload saved = repository.save(workload);

        log.info("[TX-END] Successfully completed workload transaction for trainer: '{}'. Saved document ID: '{}'",
                username, saved.getTrainerUsername());
    }

    public TrainerWorkload getTrainerWorkload(String username) {
        log.debug("Retrieving workload summary for trainer: '{}'", username);
        return repository.findByTrainerUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer workload not found with username: " + username));
    }

    private int calculateDuration(int currentDuration, int durationDelta, ActionType action) {
        if (action == ActionType.ADD) {
            return currentDuration + durationDelta;
        } else if (action == ActionType.DELETE) {
            return Math.max(0, currentDuration - durationDelta);
        }
        throw new IllegalArgumentException("Unsupported action type: " + action);
    }
}