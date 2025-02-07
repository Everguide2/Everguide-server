package com.example.everguide.web.controller;


import com.example.everguide.api.ApiResponse;
import com.example.everguide.api.code.status.SuccessStatus;
import com.example.everguide.domain.Job;
import com.example.everguide.service.job.JobDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class JobController {
    private final JobDataService jobDataService;

    @GetMapping("/api/job")
    public Mono<ResponseEntity<ApiResponse<List<Job>>>> getJobData() {
        return jobDataService.fetchAndSaveJobData()
                .map(entities -> ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, entities)));
    }
}
