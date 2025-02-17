package com.example.everguide.service.job;

import com.example.everguide.api.code.status.ErrorStatus;
import com.example.everguide.api.exception.GeneralException;
import com.example.everguide.domain.Job;
import com.example.everguide.repository.JobRepository;
import com.example.everguide.web.dto.job.JobDetailResponse;
import com.example.everguide.web.dto.job.JobItemDetail;
import com.example.everguide.web.dto.job.JobListResponse;
import com.example.everguide.web.dto.job.JobResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobDataService {
    private final JobService jobService;
    private final JobRepository jobRepository;
    private final WebClient webClient;
    private final JobMappingService jobMappingService;

    @Value("${api.service.jobKey}")
    private String jobServiceKey;
    private static final String JOBLIST_API_ENDPOINT = "/getJobList";
    private static final String JOBDETAIL_API_ENDPOINT = "/getJobInfo";

    //일자리 리스트 조회
    @Transactional
    // 외부 API 호출 → DTO 매핑 → Entity 변환 및 DB 저장 과정을 수행
    public Mono<List<Job>> fetchAndSaveJobData() {
        return webClient.get() //get 요청
                .uri(uriBuilder -> uriBuilder
                        .path(JOBLIST_API_ENDPOINT)
                        .queryParam("ServiceKey", jobServiceKey)
//                        .queryParam("numOfRows", 10)
//                        .queryParam("pageNo",1)
                        .build())
                .accept(MediaType.APPLICATION_XML) //응답 타입을 xml로 설정
                .retrieve() //응답을 수신할 준비
                .bodyToMono(JobListResponse.class)
                .flatMap(response -> {
                    if ("00".equals(response.getHeader().getResultCode())) {
                        //응답이 정상이라면, 변환
                        List<Job> jobList = jobMappingService.convert(response.getBody().getJobList());
                        //중복처리
                        List<Job> filteredJobList = filterDuplicateJobs(jobList);
                        //<---------- 만약 상세 정보를 DB에 집어넣으면, 여기에 메소드 삽입 예정
                        jobRepository.saveAll(filteredJobList); //db에 저장
                        return Mono.just(filteredJobList);
                    } else {
                        return Mono.error(new RuntimeException("API 호출 실패 : " + response.getHeader().getResultMessage()));
                    }
                });

    }

    //DB에 중복되는 직업을 제외한 직업을 리스트에 담아 리턴
    private List<Job> filterDuplicateJobs(List<Job> newJobList) {
        List<String> newJobCodeList = newJobList.stream().map(Job::getJobCode).toList(); //새로운 직업 리스트에서 직업 코드만 추출
        Set<String> duplicateJobCodeSet = jobRepository.findDuplicateJobCodeList(newJobCodeList); //DB에 이미 존재하는 직업 코드만 필터링
        List<Job> filteredJobs = new ArrayList<>();
        newJobList.forEach(job -> {
            if (duplicateJobCodeSet.contains(job.getJobCode())) {
                log.info("Duplicate jobCode found: " + job.getJobCode()); //중복되면 로그 처리
            } else {
                filteredJobs.add(job);
            }
        });
        return filteredJobs;
    }


    //일자리 상세 조회
    @Transactional
    public Mono<JobResponse.GetJobDetailDto> fetchJobDetailData(Long jobId) {
        //일자리 조회
        Job job = jobService.getJobById(jobId);
        return webClient.get() // get 요청
                .uri(uriBuilder -> uriBuilder
                        .path(JOBDETAIL_API_ENDPOINT)
                        .queryParam("ServiceKey", jobServiceKey)
                        .queryParam("id", job.getJobCode())
                        .build())
                .accept(MediaType.APPLICATION_XML) // 응답 타입을 XML로 설정
                .retrieve() // 응답을 수신할 준비
                .bodyToMono(JobDetailResponse.class) // 응답을 JobDetailResponse로 변환
                .flatMap(response -> {
                    if ("00".equals(response.getHeader().getResultCode())) {
                        // 응답이 정상이라면 변환
                        return Mono.just(JobMappingService.convertToDetailResponse(response.getBody().getJobDetail().get(0), job));
                    } else {
                        // 오류 처리 시 Mono.error 사용
                        return Mono.error(new RuntimeException("API 호출 실패: " + response.getHeader().getResultMessage()));
                    }
                });

    }
}








