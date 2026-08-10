package com.example.gym_crm.common;

import com.example.gym_crm.common.rate_limiting.LoginRateLimitFilter;
import com.example.gym_crm.common.security.jwt.JwtAuthenticationFilter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public abstract class BaseControllerTest {


    @MockitoBean
    protected LoginRateLimitFilter loginRateLimitFilter;

    @MockitoBean
    protected JwtAuthenticationFilter jwtAuthenticationFilter;
}