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
import io.swagger.v3.oas.annotations.Operation;
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
@Operation(summary = "일자리 리스트 지역 필터 수 조회", description = "일자리 리스트 조회 시, 왼쪽 필터링의 지역 필터의 수를 조회합니다..")
@GetMapping("/jobs/getCountByRegion")
    public ResponseEntity<ApiResponse<JobResponse.GetJobCountByRegionDto>> getCountByRegion() {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, jobService.getJobCountByRegion()));

    }

    @Operation(summary = "일자리 리스트 DB 적재", description = "일자리 리스트 db 적재합니다.")
    @GetMapping("/api/job")
    public Mono<ResponseEntity<ApiResponse<List<Job>>>> getJobData() {
        return jobDataService.fetchAndSaveJobData()
                .map(entities -> ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, entities)));
    }

    @Operation(summary = "기본 일자리 상세정보 조회", description = "일자리 상세정보를 조회합니다.")
    @GetMapping("/jobs/{jobId}")
    public Mono<ResponseEntity<ApiResponse<JobResponse.GetJobDetailDto>>> getJob(@PathVariable Long jobId) {
        return jobDataService.fetchJobDetailData(jobId)
                .map(entity -> ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, entity)));
    }

    @Operation(summary = "홈화면 이번주의 일자리 조회", description = "홈화면에 이번주의 일자리를 조회합니다.")
    @GetMapping("/jobs/thisWeekJob")
    public ResponseEntity<ApiResponse<JobResponse.ThisWeekJobsDto>> getThisWeekJob() {
        List<Job> thisWeekJobList = jobService.getThisWeekJobList();
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, JobMappingService.toThisWeekJobsDto(thisWeekJobList)));
    }

    @Operation(summary = "기본 일자리 리스트 조회", description = "일자리 리스트를 조회합니다. 지역, 정렬조건(startDate, endDate), 북마크여부, 페이지, 페이지에 들어갈 개수, 검색 키워드를 받습니다.")
    @GetMapping("jobs/getJobList")
    public ResponseEntity<ApiResponse<JobResponse.GetJobList>> getJobList(@RequestParam(value = "regions", required = false) List<Region> regionList,
                                                                          @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
                                                                          @RequestParam(value = "recruiting", required = false) Boolean isRecruiting,
                                                                          @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                          @RequestParam(value = "size", required = false, defaultValue = "21") Integer size,
                                                                          @RequestParam(value = "keyWord", required = false) String keyWord) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sortBy));
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, jobService.noLoginGetJobListResult(regionList, sortBy, isRecruiting, pageable,keyWord)));
    }
    @Operation(summary = "기본 일자리 이름 검색", description = "기본 일자리 이름 검색입니다. 검색 키워드, 페이지, 페이지에 들어갈 개수, 검색 키워드를 받습니다.")
    @GetMapping("/jobs/getJobListSearchByName")
    public ResponseEntity<ApiResponse<JobResponse.GetJobListSearchByName>> noLoginGetJobListSearchByName(@RequestParam(value = "name") String name,
                                                                         @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                         @RequestParam(value = "size", required = false, defaultValue = "4") Integer size
                                                                         ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, jobService.noLoginSearchJobListByName(name, pageable)));
    }
    @Operation(summary = "기본 일자리 이름 검색 시 총 일자리 수 조회", description = "기본 일자리 이름 시, 검색 키워드에 해당하는 ")
    @GetMapping("/jobs/getTotalCountByName")
    public ResponseEntity<ApiResponse<Integer>> getTotalCountSearchByName(@RequestParam(value = "name") String name) {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, jobService.getTotalCountSearchByName(name)));

    }


//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
//    //로그인 했을 때만 가능

    @Operation(summary = "로그인한 회원 일자리 북마크 여부 조회", description = "로그인 한 회원이 해당 일자리를 북마크 했는 지 여부를 조회합니다.")
    @GetMapping("/member/jobs/isBookMarked/{jobId}")
    public ResponseEntity<ApiResponse<Boolean>> getJobListSearchByName(@PathVariable(value = "jobId") Long jobId) {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, jobService.isBookmarked(jobId)));
    }

    @Operation(summary = "로그인한 회원 일자리 이름 검색", description = "로그인한 회원의 일자리 이름 검색입니다. 검색 키워드, 페이지, 페이지에 들어갈 개수, 검색 키워드를 받습니다. 북마크 여부를 추가로 반환합니다.")
    @GetMapping("member/jobs/getJobListSearchByName")
    public ResponseEntity<ApiResponse<JobResponse.GetJobListSearchByName>> getJobListSearchByName(@RequestParam(value = "name") String name,
                                                                                                  @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                                                  @RequestParam(value = "size", required = false, defaultValue = "4") Integer size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, jobService.SearchJobListByName(name, pageable)));
    }

    @Operation(summary = "로그인한 회원 일자리 리스트 조회", description = "로그인한 회원의 일자리 리스트를 조회합니다. 지역, 정렬조건(startDate, endDate), 북마크여부, 페이지, 페이지에 들어갈 개수, 검색 키워드를 받습니다. 북마크 여부를 추가로 반환합니다.")
    @GetMapping("member/jobs/getJobList")
    public ResponseEntity<ApiResponse<JobResponse.GetJobList>> loginGetJobList(@RequestParam(value = "regions", required = false) List<Region> regionList,
                                                                               @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
                                                                               @RequestParam(value = "recruiting", required = false) Boolean isRecruiting,
                                                                               @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                               @RequestParam(value = "size", required = false, defaultValue = "21") Integer size,
                                                                               @RequestParam(value = "keyWord", required = false) String keyWord) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sortBy));

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, jobService.getJobListResult(regionList, sortBy, isRecruiting, pageable,keyWord)));
    }

    @Operation(summary = "로그인한 회원 일자리 북마크 삭제", description = "로그인 한 회원이 해당 일자리를 북마크 삭제합니다.")
    @DeleteMapping("member/jobs/deleteJobBookmark")
    public ResponseEntity<ApiResponse<JobResponse.deleteJobBookmarkResultDto>> deleteBookMark(@ModelAttribute JobRequest.deleteJobBookmarkDto request) {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, JobMappingService.toDeleteJobBookmarkResultDto(jobService.deleteJobBookmark(request))));
    }
    @Operation(summary = "로그인한 회원 일자리 북마크 추가", description = "로그인 한 회원이 해당 일자리를 북마크 추가합니다.")
    @PostMapping("member/jobs/addJobBookmark")
    public ResponseEntity<ApiResponse<JobResponse.addJobBookmarkResultDto>> addJobBookMark(@ModelAttribute JobRequest.addJobBookmarkDto request) {
        /// 여기서 시큐리티 authentication을 memberUserId 를 가지고 와야한다  + 리포지토리 조회할 때, 로그인 아이디로 조회
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, JobMappingService.toAddJobBookMarkResponseResultDto(jobService.addJobBookmark(request))));

    }
}
