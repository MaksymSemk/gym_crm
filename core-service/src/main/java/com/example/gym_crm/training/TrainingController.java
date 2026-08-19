package com.example.gym_crm.training;

import com.example.gym_crm.training.Dto.TrainingCreateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/trainings")
@RequiredArgsConstructor
@Tag(name = "Training Management", description = "Endpoints for managing training sessions")
public class TrainingController {

    private final TrainingService trainingService;

    @Operation(summary = "Add Training", description = "Creates a new training session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training session created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload"),
            @ApiResponse(responseCode = "404", description = "Trainee or Trainer not found")
    })
    @PostMapping
    public ResponseEntity<Void> addTraining(@Valid @RequestBody TrainingCreateDto dto) {
        trainingService.createTraining(dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}