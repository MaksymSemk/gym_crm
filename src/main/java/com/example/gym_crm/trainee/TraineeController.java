package com.example.gym_crm.trainee;

import com.example.gym_crm.trainee.Dto.TraineeStatusUpdateDto;
import com.example.gym_crm.trainee.Dto.TraineeTrainingsSearchDto;
import com.example.gym_crm.trainee.Dto.TraineeUpdateDto;
import com.example.gym_crm.trainee.Dto.TraineeUpdateTrainersDto;
import com.example.gym_crm.trainee.Dto.responce.TraineeGetResponse;
import com.example.gym_crm.trainee.Dto.responce.TraineeTrainingResponse;
import com.example.gym_crm.trainee.Dto.responce.TrainerGetResponse;
import com.example.gym_crm.trainee.mapper.TraineeMapper;
import com.example.gym_crm.trainer.Trainer;
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
@RequestMapping("api/v1/trainees")
@RequiredArgsConstructor
@Tag(name = "Trainee Management", description = "Endpoints for managing trainee profiles and operations")
public class TraineeController {

    private final TraineeService traineeService;
    private final TraineeMapper traineeMapper;

    @Operation(summary = "Get Trainee Profile", description = "Retrieves trainee details by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched profile"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping("/{username}")
    public ResponseEntity<TraineeGetResponse> getTraineeProfile(@PathVariable String username) {
        Trainee trainee = traineeService.getTraineeByUsername(username);
        return ResponseEntity.ok(traineeMapper.toTraineeGetResponse(trainee));
    }

    @Operation(summary = "Update Trainee Profile", description = "Updates profile details for an existing trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated profile"),
            @ApiResponse(responseCode = "400", description = "Invalid payload"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PutMapping
    public ResponseEntity<TraineeGetResponse> updateTraineeProfile(@Valid @RequestBody TraineeUpdateDto dto) {
        Trainee updatedTrainee = traineeService.updateTrainee(dto);
        return ResponseEntity.ok(traineeMapper.toTraineeGetResponse(updatedTrainee));
    }

    @Operation(summary = "Delete Trainee Profile", description = "Deletes a trainee profile by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted profile"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTraineeProfile(@PathVariable String username) {
        traineeService.deleteTraineeByUsername(username);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Get Active Trainers Not Assigned to Trainee", description = "Fetches active trainers available for assignment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping("/{username}/unassigned-trainers")
    public ResponseEntity<List<TrainerGetResponse>> getUnassignedActiveTrainers(@PathVariable String username) {
        List<Trainer> trainers = traineeService.getUnassignedActiveTrainers(username);
        return ResponseEntity.ok(traineeMapper.toTrainerGetResponseList(trainers));
    }

    @Operation(summary = "Update Trainee's Trainer List", description = "Re-assigns a list of trainers to a trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainers updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload")
    })
    @PutMapping("/trainers")
    public ResponseEntity<List<TrainerGetResponse>> updateTraineeTrainers(@Valid @RequestBody TraineeUpdateTrainersDto dto) {
        Trainee updatedTrainee = traineeService.updateTraineeTrainers(dto);
        return ResponseEntity.ok(traineeMapper.toTrainerGetResponseList(updatedTrainee.getTrainers()));
    }

    @Operation(summary = "Get Trainee Trainings List", description = "Retrieves trainings by search criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully")
    })
    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TraineeTrainingResponse>> getTraineeTrainings(
            @PathVariable String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) String trainingType
    ) {
        TraineeTrainingsSearchDto searchDto = new TraineeTrainingsSearchDto();
        searchDto.setUsername(username);
        searchDto.setFromDate(periodFrom);
        searchDto.setToDate(periodTo);
        searchDto.setTrainerName(trainerName);
        searchDto.setTrainingType(trainingType);

        List<Training> trainings = traineeService.getTraineeTrainings(searchDto);
        return ResponseEntity.ok(traineeMapper.toTraineeTrainingResponseList(trainings));
    }

    @Operation(summary = "Activate/Deactivate Trainee", description = "Changes active status of a trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status toggled successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PatchMapping("/status")
    public ResponseEntity<Void> toggleTraineeStatus(@Valid @RequestBody TraineeStatusUpdateDto dto) {
        traineeService.updateTraineeStatus(dto.username());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}