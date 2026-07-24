package com.example.gym_crm.trainer;

import com.example.gym_crm.trainer.Dto.TrainerCreateDto;
import com.example.gym_crm.trainer.Dto.TrainerStatusUpdateDto;
import com.example.gym_crm.trainer.Dto.TrainerTrainingsSearchDto;
import com.example.gym_crm.trainer.Dto.TrainerUpdateDto;
import com.example.gym_crm.trainer.Dto.response.TrainerCreatedResponse;
import com.example.gym_crm.trainer.Dto.response.TrainerGetResponse;
import com.example.gym_crm.trainer.Dto.response.TrainerTrainingResponse;
import com.example.gym_crm.trainer.mapper.TrainerMapper;
import com.example.gym_crm.training.Training;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/v1/trainers")
@RequiredArgsConstructor
@Tag(name = "Trainer Management", description = "Endpoints for managing trainer profiles and operations")
public class TrainerController {

    private final TrainerService trainerService;
    private final TrainerMapper trainerMapper;

    @Operation(summary = "Trainer Registration", description = "Creates a new trainer profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainer registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload")
    })
    @PostMapping
    public ResponseEntity<TrainerCreatedResponse> registerTrainer(@Valid @RequestBody TrainerCreateDto dto) {
        Trainer createdTrainer = trainerService.createTrainer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(trainerMapper.toTrainerCreatedResponse(createdTrainer));
    }

    @Operation(summary = "Get Trainer Profile", description = "Retrieves trainer details by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched profile"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @GetMapping("/{username}")
    public ResponseEntity<TrainerGetResponse> getTrainerProfile(@PathVariable String username) {
        Trainer trainer = trainerService.getTrainerByUsername(username);
        return ResponseEntity.ok(trainerMapper.toTrainerGetResponse(trainer));
    }

    @Operation(summary = "Update Trainer Profile", description = "Updates profile details for an existing trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated profile"),
            @ApiResponse(responseCode = "400", description = "Invalid payload"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @PutMapping
    public ResponseEntity<TrainerGetResponse> updateTrainerProfile(@Valid @RequestBody TrainerUpdateDto dto) {
        Trainer updatedTrainer = trainerService.updateTrainer(dto);
        return ResponseEntity.ok(trainerMapper.toTrainerGetResponse(updatedTrainer));
    }

    @Operation(summary = "Get Trainer Trainings List", description = "Retrieves trainer trainings by search criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainerTrainingResponse>> getTrainerTrainings(
            @PathVariable String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam(required = false) String traineeName
    ) {
        TrainerTrainingsSearchDto searchDto = new TrainerTrainingsSearchDto();
        searchDto.setUsername(username);
        searchDto.setFromDate(periodFrom);
        searchDto.setToDate(periodTo);
        searchDto.setTraineeName(traineeName);

        List<Training> trainings = trainerService.getTrainerTrainings(searchDto);
        return ResponseEntity.ok(trainerMapper.toTrainerTrainingResponseList(trainings));
    }

    @Operation(summary = "Activate/De-Activate Trainer", description = "Toggles active status of a trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @PatchMapping("/status")
    public ResponseEntity<Void> toggleTrainerStatus(@Valid @RequestBody TrainerStatusUpdateDto dto) {
        trainerService.updateTrainerStatus(dto.username());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}