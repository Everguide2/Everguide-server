package com.example.everguide.service.education;

import com.example.everguide.api.code.status.ErrorStatus;
import com.example.everguide.api.exception.GeneralException;
import com.example.everguide.domain.Education;
import com.example.everguide.repository.EducationRepository;
import com.example.everguide.web.dto.education.EducationDetailResponse;
import com.example.everguide.web.dto.education.EducationListResponse;
import com.example.everguide.web.dto.education.EducationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class EducationDataService {

    private final EducationService educationService;
    private final EducationRepository educationRepository;
    private final EducationMappingService educationMappingService;

    @Qualifier("educationWebClient")
    private final WebClient educationWebClient;

    @Value("${api.service.job-key}")
    private String serviceKey;
    private static final String API_ENDPOINT = "/getEduInfoList";
    private static final String EDUCATION_DETAIL_API_ENDPOINT = "/getEduInfo";

    //일자리 리스트 조회
    @Transactional
    // 외부 API 호출 → DTO 매핑 → Entity 변환 및 DB 저장 과정을 수행
    public Mono<List<Education>> fetchAndSaveEducationData() {

        return educationWebClient
                .get() //get 요청
                .uri(uriBuilder -> uriBuilder
                        .path(API_ENDPOINT)
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("numOfRows", 10)
                        .queryParam("pageNo", 1)
                        .build())
                .accept(MediaType.APPLICATION_XML) //응답 타입을 xml로 설정
                .retrieve() //응답을 수신할 준비
                .bodyToMono(EducationListResponse.class)
                .flatMap(response -> {
                    if ("00".equals(response.getHeader().getResultCode())) {
                        //응답이 정상이라면, 변환
                        List<Education> educationList = educationMappingService.convert(response.getBody().getEducationList());
                        //중복처리
                        List<Education> filteredJobList = filterDuplicateEducations(educationList);
                        educationRepository.saveAll(filteredJobList); //db에 저장
                        log.info("Successfully fetched and saved {} jobs", filteredJobList.size());
                        return Mono.just(filteredJobList);
                    } else {
                        log.error("API call failed: {}", response.getHeader().getResultMessage());
                        return Mono.error(new RuntimeException("API 호출 실패: " + response.getHeader().getResultMessage()));
                    }
                });

    }

//    //DB에 중복되는 직업을 제외한 직업을 리스트에 담아 리턴
    private List<Education> filterDuplicateEducations(List<Education> educationList) {
        List<String> newEducationKey1List = educationList.stream().map(Education::getEducationKey1).toList(); // 새로운 교육 리스트에서 educationKey1만 추출
        List<String> newEducationKey2List = educationList.stream().map(Education::getEducationKey2).toList(); // 새로운 교육 리스트에서 educationKey2만 추출

        // 두 key 리스트를 동시에 사용하여 중복된 key를 필터링
        Set<String> duplicateEduCodeSet = educationRepository.findDuplicateEducationKeyList(newEducationKey1List, newEducationKey2List);

        List<Education> filteredJobs = new ArrayList<>();
        educationList.forEach(education -> {
            // educationKey1과 educationKey2를 조합하여 중복 체크
            String combinedKey = education.getEducationKey1() + "-" + education.getEducationKey2();

            if (duplicateEduCodeSet.contains(combinedKey)) {
                log.info("Duplicate jobCode found: {}", combinedKey); // 중복되면 로그 처리
            } else {
                filteredJobs.add(education);
            }
        });
        return filteredJobs;
    }

//
    @Transactional
    public Mono<EducationResponse.GetEduDetailDto> fetchEducationDetailData(Long educationId) {
        Education education = educationRepository.findById(educationId).orElseThrow(() -> new GeneralException(ErrorStatus._EDUCATION_NOT_FOUND));
        return educationWebClient.get() // get 요청
                .uri(uriBuilder -> uriBuilder
                        .path(EDUCATION_DETAIL_API_ENDPOINT)
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("eduCrseNo", education.getEducationKey1())
                        .queryParam("lctreNo", education.getEducationKey2())
                        .build())
                .accept(MediaType.APPLICATION_XML) // 응답 타입을 XML로 설정
                .retrieve() // 응답을 수신할 준비
                .bodyToMono(EducationDetailResponse.class) // 응답을 JobDetailResponse로 변환
                .flatMap(response -> {
                    if ("00".equals(response.getHeader().getResultCode())) {
                        // 응답이 정상이라면 변환
                        return Mono.just(EducationMappingService.convertToDetailEduDetailDto(response.getBody().getJobDetail().get(0)));
                    } else {
                        // 오류 처리 시 Mono.error 사용
                        return Mono.error(new RuntimeException("API 호출 실패: " + response.getHeader().getResultMessage()));
                    }
                });

    }
}

