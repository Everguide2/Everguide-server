package com.example.everguide.web.controller;


import com.example.everguide.api.ApiResponse;
import com.example.everguide.api.code.status.SuccessStatus;
import com.example.everguide.domain.Job;
import com.example.everguide.domain.enums.Region;
import com.example.everguide.service.job.JobDataService;
import com.example.everguide.service.job.JobMappingService;
import com.example.everguide.service.job.JobService;
import com.example.everguide.web.dto.job.JobResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class JobController {
    private final JobDataService jobDataService;
    private final JobService jobService;

    @GetMapping("/api/job")
    public Mono<ResponseEntity<ApiResponse<List<Job>>>> getJobData() {
        return jobDataService.fetchAndSaveJobData()
                .map(entities -> ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, entities)));
    }

    @GetMapping("/jobs")
    public ApiResponse<JobResponse.GetJobList> getJobList(@RequestParam(value = "regions", required = false) List<Region> regionList,
                                                          @RequestParam(value = "sortBy", required = false,defaultValue = "id") String sortBy,
                                                          @RequestParam(value = "recruiting", required = false) Boolean isRecruiting,
                                                          @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                          @RequestParam(value = "size", required = false, defaultValue = "21") Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sortBy));
        List<Job> jobList = jobService.getJobList(regionList, sortBy, isRecruiting, pageable);
        return ApiResponse.onSuccess(SuccessStatus._OK, JobMappingService.toJobListDto(jobList));
    }
}
