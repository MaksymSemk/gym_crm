package com.example.gym_crm.authentication;

import com.example.gym_crm.authentication.dto.AuthResponseDto;
import com.example.gym_crm.authentication.dto.ChangePasswordRequestDto;
import com.example.gym_crm.authentication.dto.LoginRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    AuthResponseDto login(LoginRequestDto dto);
    void changePassword(ChangePasswordRequestDto dto);
    void logout(HttpServletRequest request);
}