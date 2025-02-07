package com.example.everguide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing //BaseEntity 처리하기 위한 에노테이션
@SpringBootApplication
public class EverguideApplication {

    public static void main(String[] args) {
        SpringApplication.run(EverguideApplication.class, args);
    }
}
