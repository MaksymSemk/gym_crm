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

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerWorkloadService {

    private final TrainerWorkloadRepository repository;

    public synchronized void processWorkload(TrainerWorkloadRequestDto request) {
        String username = request.trainerUsername();
        int reqYear = request.trainingDate().getYear();
        int reqMonth = request.trainingDate().getMonthValue();
        int duration = request.trainingDuration();
        ActionType action = request.actionType();

        log.debug("Operation [1/5]: Looking up workload entity for trainer: '{}'", username);
        TrainerWorkload workload = repository.findByUsername(username)
                .orElseGet(() -> {
                    log.debug("Operation [1/5]: Workload record not found. Initializing new record for '{}'", username);
                    TrainerWorkload newWorkload = new TrainerWorkload();
                    newWorkload.setTrainerUsername(username);
                    return newWorkload;
                });

        log.debug("Operation [2/5]: Updating trainer status and metadata");
        workload.setTrainerFirstName(request.trainerFirstName());
        workload.setTrainerLastName(request.trainerLastName());
        workload.setTrainerStatus(request.isActive());

        log.debug("Operation [3/5]: Resolving YearSummary for year: {}", reqYear);
        YearSummary yearSummary = workload.getYears().stream()
                .filter(y -> y.getYear() == reqYear)
                .findFirst()
                .orElseGet(() -> {
                    log.debug("Operation [3/5]: Adding new YearSummary entry for year: {}", reqYear);
                    YearSummary y = new YearSummary();
                    y.setYear(reqYear);
                    workload.getYears().add(y);
                    return y;
                });

        log.debug("Operation [4/5]: Resolving MonthSummary for month: {}", reqMonth);
        MonthSummary monthSummary = yearSummary.getMonths().stream()
                .filter(m -> m.getMonthNumber() == reqMonth)
                .findFirst()
                .orElseGet(() -> {
                    log.debug("Operation [4/5]: Initializing MonthSummary entry for month: {}", reqMonth);
                    MonthSummary m = new MonthSummary(reqMonth, 0);
                    yearSummary.getMonths().add(m);
                    return m;
                });

        int previousDuration = monthSummary.getTrainingSummaryDuration();
        if (action == ActionType.ADD) {
            monthSummary.setTrainingSummaryDuration(previousDuration + duration);
        } else if (action == ActionType.DELETE) {
            monthSummary.setTrainingSummaryDuration(Math.max(0, previousDuration - duration));
        }
        log.debug("Operation [4/5]: Recalculated duration. (Prev: {}m, Action: {}, Change: {}m, New: {}m)",
                previousDuration, action, duration, monthSummary.getTrainingSummaryDuration());

        log.debug("Operation [5/5]: Persisting updated workload in repository");
        repository.save(workload);
        log.info("Operation Complete: Workload record successfully saved for trainer '{}'", username);
    }

    public TrainerWorkload getTrainerWorkload(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found with username: " + username));
    }
}