package com.example.gym_crm.authentication.dto;

public record AuthResponseDto(
        String username,
        String token
) {}