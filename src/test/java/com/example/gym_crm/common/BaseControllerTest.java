package com.example.gym_crm.common;

import com.example.gym_crm.common.get_ip.GetIpService;
import com.example.gym_crm.common.rate_limiting.LoginRateLimitFilter;
import com.example.gym_crm.common.security.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

public abstract class BaseControllerTest {

    @MockitoBean
    protected GetIpService getIpService;

    @MockitoBean
    protected LoginRateLimitFilter loginRateLimitFilter;

    @MockitoBean
    protected JwtAuthenticationFilter jwtAuthenticationFilter;
}