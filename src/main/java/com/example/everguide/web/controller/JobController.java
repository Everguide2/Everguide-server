package com.example.everguide.web.controller;


import com.example.everguide.api.ApiResponse;
import com.example.everguide.api.code.status.SuccessStatus;
import com.example.everguide.domain.Job;
import com.example.everguide.domain.enums.Region;
import com.example.everguide.service.job.JobDataService;
import com.example.everguide.service.job.JobMappingService;
import com.example.everguide.service.job.JobService;
import com.example.everguide.web.dto.job.JobRequest;
import com.example.everguide.web.dto.job.JobResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/jobs/{jobId}")
    public Mono<ResponseEntity<ApiResponse<JobResponse.GetJobDetailDto>>> getJob(@PathVariable Long jobId) {

        return jobDataService.fetchJobDetailData(jobId)
                .map(entity -> ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, entity)));

    }

    @GetMapping("/jobs/getJobList")
    public ResponseEntity<ApiResponse<JobResponse.GetJobList>> getJobList(@RequestParam(value = "regions", required = false) List<Region> regionList,
                                                                          @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
                                                                          @RequestParam(value = "recruiting", required = false) Boolean isRecruiting,
                                                                          @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                          @RequestParam(value = "size", required = false, defaultValue = "21") Integer size,
                                                                          @RequestParam(value = "memberId") Long memberId) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sortBy));

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, jobService.getJobListResult(regionList, sortBy, isRecruiting, pageable, memberId)));
    }


    @GetMapping("/jobs/thisWeekJob")
    public ResponseEntity<ApiResponse<JobResponse.ThisWeekJobsDto>> getThisWeekJob() {
        List<Job> thisWeekJobList = jobService.getThisWeekJobList();
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, JobMappingService.toThisWeekJobsDto(thisWeekJobList)));


    }

    @PostMapping("/jobs/addJobBookmark")
    public ResponseEntity<ApiResponse<JobResponse.addJobBookmarkResultDto>> addJobBookMark(@ModelAttribute JobRequest.addJobBookmarkDto request) {
        /// 여기서 시큐리티 authentication을 memberUserId 를 가지고 와야한다  + 리포지토리 조회할 때, 로그인 아이디로 조회
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, JobMappingService.toAddJobBookMarkResponseResultDto(jobService.addJobBookmark(request))));

    }

    @DeleteMapping("/jobs/deleteJobBookmark")
    public ResponseEntity<ApiResponse<JobResponse.deleteJobBookmarkResultDto>> deleteBookMark(@ModelAttribute JobRequest.deleteJobBookmarkDto request) {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, JobMappingService.toDeleteJobBookmarkResultDto(jobService.deleteJobBookmark(request))));
    }
}
