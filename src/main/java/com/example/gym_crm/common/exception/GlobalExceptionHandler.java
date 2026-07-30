package com.example.gym_crm.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ErrorMessage> handleEntityAlreadyExistsException(
            EntityAlreadyExistsException ex, HttpServletRequest request) {

        logWarn(request, ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorMessage("An error occurred: " + ex.getMessage()));
    }

    @ExceptionHandler(EntityDoesNotExistException.class)
    public ResponseEntity<ErrorMessage> handleEntityDoesNotExistException(
            EntityDoesNotExistException ex, HttpServletRequest request) {

        logWarn(request, ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage("An error occurred: " + ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorMessage> handleBadCredentialsException(
            BadCredentialsException ex, HttpServletRequest request) {

        log.warn("Authentication failed on [{} {}]: {}",
                request.getMethod(), request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorMessage("Invalid username or password"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String errorDetails = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation failed on [{} {}]: {}",
                request.getMethod(), request.getRequestURI(), errorDetails);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage("Validation error: " + errorDetails));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleException(
            Exception ex, HttpServletRequest request) {

        log.error("Unhandled Exception processing [{} {}]",
                request.getMethod(), request.getRequestURI(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessage("An unexpected internal error occurred"));
    }

    private void logWarn(HttpServletRequest request, Exception ex) {
        log.warn("Business Exception on [{} {}]: {}",
                request.getMethod(), request.getRequestURI(), ex.getMessage());
    }
}