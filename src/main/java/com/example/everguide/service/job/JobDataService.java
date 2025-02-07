package com.example.everguide.service.job;

import com.example.everguide.domain.Job;
import com.example.everguide.repository.JobRepository;
import com.example.everguide.web.dto.job.JobListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.http.MediaType;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobDataService {
    private final JobRepository jobRepository;
    private final WebClient webClient;
    private final JobMappingService jobMappingService;
    @Value("${api.service.jobKey}")
    private String jobServiceKey;
    private static final String API_ENDPOINT = "/getJobBsnRecruitList";

    @Transactional
    // 외부 API 호출 → DTO 매핑 → Entity 변환 및 DB 저장 과정을 수행
    public Mono<List<Job>> fetchAndSaveJobData() {
        return webClient.get() //get 요청
                .uri(uriBuilder -> uriBuilder
                        .path(API_ENDPOINT)
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
                        jobRepository.saveAll(jobList); //db에 저장
                        return Mono.just(jobList);
                    } else {
                        return Mono.error(new RuntimeException("API 호출 실패 : " + response.getHeader().getResultMessage()));
                    }
                });

    }

}
