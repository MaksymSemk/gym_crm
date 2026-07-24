package com.example.gym_crm.authentication;

import com.example.gym_crm.authentication.dto.ChangePasswordRequestDto;
import com.example.gym_crm.authentication.dto.LoginRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    void login(LoginRequestDto dto, HttpServletRequest request, HttpServletResponse response);
    void changePassword(ChangePasswordRequestDto dto);
}