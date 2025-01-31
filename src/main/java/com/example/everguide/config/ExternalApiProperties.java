package com.example.everguide.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "external.api")
public class ExternalApiProperties {
    
    private Policy policy;
    private Education education;
    private Job job;

    @Getter
    @Setter
    public static class Policy {
        private String baseUrl;
        private String serviceKey;
    }

    @Getter
    @Setter
    public static class Education {
        private String baseUrl;
        private String serviceKey;
    }

    @Getter
    @Setter
    public static class Job {
        private String baseUrl;
        private String serviceKey;
    }
} 