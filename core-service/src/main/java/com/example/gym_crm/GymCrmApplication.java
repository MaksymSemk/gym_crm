package com.example.gym_crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class GymCrmApplication {

    public static void main(String[] args) {
        SpringApplication.run(GymCrmApplication.class, args);
    }

}
