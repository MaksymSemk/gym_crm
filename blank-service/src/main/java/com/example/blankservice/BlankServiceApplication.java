package com.example.blankservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class BlankServiceApplication {

    private int counter = 0;

    public static void main(String[] args) {
        SpringApplication.run(BlankServiceApplication.class, args);
    }

    @Scheduled(fixedRate = 2000)
    public void logHeartbeat() {
        log.info("Heartbeat #{} at {}", ++counter, LocalDateTime.now());
    }

}
