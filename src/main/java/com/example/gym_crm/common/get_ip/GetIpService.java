package com.example.gym_crm.common.get_ip;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class GetIpService {

    public String getRemoteIP(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf == null || xf.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xf.split(",")[0].trim();
    }
}
