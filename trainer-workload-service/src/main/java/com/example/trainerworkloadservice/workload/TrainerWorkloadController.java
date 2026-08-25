package com.example.trainerworkloadservice.workload;

import com.example.trainerworkloadservice.workload.dto.TrainerWorkloadRequestDto;
import com.example.trainerworkloadservice.workload.model.TrainerWorkload;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workload")
@RequiredArgsConstructor
public class TrainerWorkloadController {

    private final TrainerWorkloadService workloadService;

    @PostMapping
    public ResponseEntity<Void> acceptTrainerWorkload(@Valid @RequestBody TrainerWorkloadRequestDto request) {
        workloadService.processWorkload(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerWorkload> getTrainerWorkload(@PathVariable String username) {
        TrainerWorkload workload = workloadService.getTrainerWorkload(username);
        return ResponseEntity.ok(workload);
    }
}