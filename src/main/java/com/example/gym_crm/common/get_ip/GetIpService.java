package com.example.gym_crm.common.get_ip;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class GetIpService {
//# can be metadata, header
//    app.ip.ip-from =${IP-FROM:header}
//    @Value("${app.ip.ip-from}")
//    private String ipFrom;

    public String getRemoteIP(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf == null || xf.isEmpty()) {
            return request.getRemoteAddr();
        }
        // Pick the first IP in the list (the actual client)
        return xf.split(",")[0].trim();
    }
}
