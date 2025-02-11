package com.example.everguide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EverguideApplication {

    public static void main(String[] args) {
        SpringApplication.run(EverguideApplication.class, args);
    }
}
