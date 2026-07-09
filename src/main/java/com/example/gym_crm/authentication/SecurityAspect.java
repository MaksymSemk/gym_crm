package com.example.gym_crm.authentication;

import com.example.gym_crm.common.user.UserRepository;
import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@AllArgsConstructor
public class SecurityAspect {

    private UserRepository userRepository;

    @Before("@annotation(requiresAuth)")
    public void authenticate(JoinPoint joinPoint, RequiresAuth requiresAuth) {
        AuthData authData = null;

        for(Object o : joinPoint.getArgs()) {
            if(o instanceof AuthData) {
                authData = (AuthData) o;
                break;
            }
        }

        if (authData == null) {
            throw new AuthenticationException("Authentication failed");
        }

        var user =  userRepository.findByUsername(authData.getUsername()).orElseThrow(
                ()-> new AuthenticationException("Authentication failed")
        );
        var password = authData.getPassword();

        if (!password.equals(user.getPassword())) {
            throw new AuthenticationException("Authentication failed");
        }
    }
}
