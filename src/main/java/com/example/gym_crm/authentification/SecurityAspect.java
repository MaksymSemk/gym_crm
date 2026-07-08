package com.example.gym_crm.authentification;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Aspect
@Component
public class SecurityAspect {

    @Before("@annotation(requiresAuth)")
    public void authenticate(JoinPoint joinPoint, RequiresAuth requiresAuth) {

    }
}
