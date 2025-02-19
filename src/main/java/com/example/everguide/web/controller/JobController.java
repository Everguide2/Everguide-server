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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class JobController {
    private final JobDataService jobDataService;
    private final JobService jobService;

// 로그인 없어도 접근 가능한 컨트롤러
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


    @GetMapping("/jobs/thisWeekJob")
    public ResponseEntity<ApiResponse<JobResponse.ThisWeekJobsDto>> getThisWeekJob() {
        List<Job> thisWeekJobList = jobService.getThisWeekJobList();
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, JobMappingService.toThisWeekJobsDto(thisWeekJobList)));
    }
    //로그인 안했을 때, 일자리 리스트 조회
    @GetMapping("jobs/getJobList")
    public ResponseEntity<ApiResponse<JobResponse.GetJobList>> getJobList(@RequestParam(value = "regions", required = false) List<Region> regionList,
                                                                          @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
                                                                          @RequestParam(value = "recruiting", required = false) Boolean isRecruiting,
                                                                          @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                          @RequestParam(value = "size", required = false, defaultValue = "21") Integer size){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sortBy));
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, jobService.noLoginGetJobListResult(regionList, sortBy, isRecruiting, pageable)));
    }
    // 로그인 안했을 때, 검색 기능
    @GetMapping("/jobs/getJobListSearchByName")
    public ResponseEntity<ApiResponse<JobResponse.GetJobListSearchByName>> getJobListSearchByName(@RequestParam(value = "name") String name,
                                                                         @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                         @RequestParam(value = "size", required = false, defaultValue = "5") Integer size
                                                                         ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, jobService.noLoginSearchJobListByName(name, pageable)));
    }

    @GetMapping("/jobs/getTotalCountByName")
    public ResponseEntity<ApiResponse<Integer>> getTotalCountSearchByName(@RequestParam(value = "name") String name) {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, jobService.getTotalCountSearchByName(name)));

    }


//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
//    //로그인 했을 때만 가능

    @GetMapping("/member/jobs/isBookMarked/{jobId}")
    public ResponseEntity<ApiResponse<Boolean>> getJobListSearchByName(@PathVariable(value = "jobId") Long jobId) {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, jobService.isBookmarked(jobId)));
    }

    @GetMapping("member/jobs/getJobListSearchByName")
    public ResponseEntity<ApiResponse<JobResponse.GetJobListSearchByName>> getJobListSearchByName(@RequestParam(value = "name") String name,
                                                                                                  @RequestParam(value = "memberId") Long memberId,
                                                                                                  @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                                                  @RequestParam(value = "size", required = false, defaultValue = "5") Integer size
    ) {

        Pageable pageable = PageRequest.of(page - 1, size);
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, jobService.SearchJobListByName(name, pageable)));
    }


    @GetMapping("member/jobs/getJobList")
    public ResponseEntity<ApiResponse<JobResponse.GetJobList>> loginGetJobList(@RequestParam(value = "regions", required = false) List<Region> regionList,
                                                                          @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
                                                                          @RequestParam(value = "recruiting", required = false) Boolean isRecruiting,
                                                                          @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                          @RequestParam(value = "size", required = false, defaultValue = "21") Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sortBy));

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, jobService.getJobListResult(regionList, sortBy, isRecruiting, pageable)));
    }

    @DeleteMapping("member/jobs/deleteJobBookmark")
    public ResponseEntity<ApiResponse<JobResponse.deleteJobBookmarkResultDto>> deleteBookMark(@ModelAttribute JobRequest.deleteJobBookmarkDto request) {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, JobMappingService.toDeleteJobBookmarkResultDto(jobService.deleteJobBookmark(request))));
    }
    @PostMapping("member/jobs/addJobBookmark")
    public ResponseEntity<ApiResponse<JobResponse.addJobBookmarkResultDto>> addJobBookMark(@ModelAttribute JobRequest.addJobBookmarkDto request) {
        /// 여기서 시큐리티 authentication을 memberUserId 를 가지고 와야한다  + 리포지토리 조회할 때, 로그인 아이디로 조회
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, JobMappingService.toAddJobBookMarkResponseResultDto(jobService.addJobBookmark(request))));

    }
}
