package com.example.gym_crm.authentication;

import com.example.gym_crm.authentication.dto.ChangePasswordRequestDto;
import com.example.gym_crm.authentication.dto.LoginRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Management", description = "Endpoints for user authentication and credentials")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login", description = "Authenticates user credentials and initializes HTTP session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Valid @ModelAttribute LoginRequestDto dto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authService.login(dto, request, response);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Change Login / Password", description = "Updates user password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or old password mismatch"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequestDto dto) {
        authService.changePassword(dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}