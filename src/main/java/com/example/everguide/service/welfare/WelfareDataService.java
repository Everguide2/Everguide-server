package com.example.everguide.service.welfare;

import com.example.everguide.domain.WelfareService;
import com.example.everguide.repository.WelfareServiceRepository;
import com.example.everguide.web.dto.welfare.WantedListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WelfareDataService {

    private final WelfareServiceRepository repository;
    private final WelfareMappingService welfareMappingService;

    @Qualifier("welfareWebClient")
    private final WebClient welfareWebClient;

    @Value("${api.service.welfare-key}")
    private String serviceKey;
    private static final String API_ENDPOINT = "/LcgvWelfarelist";

    @Transactional
    public Mono<List<WelfareService>> fetchAndSaveWelfareData() {
        return welfareWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_ENDPOINT)
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("numOfRows", 20)
                        .queryParam("pageNo",1)
                        .build())
                .accept(MediaType.APPLICATION_XML)
                .retrieve()
                .bodyToMono(WantedListResponse.class)
                .flatMap(response -> {
                    if ("0".equals(response.getResultCode())) {
                        List<WelfareService> entities = welfareMappingService.convert(response.getServList());
                        repository.saveAll(entities);
                        log.info("Successfully fetched and saved {} welfare services", entities.size());
                        return Mono.just(entities);
                    } else {
                        log.error("API call failed: {}", response.getResultMessage());
                        return Mono.error(new RuntimeException("API 호출 실패: " + response.getResultMessage()));
                    }
                });
    }

    // 스케줄러를 통한 주기적 호출이 필요하면, @Scheduled 어노테이션을 활용
    // @Scheduled(cron = "0 0 0 * * *")
    // public void scheduledFetchAndSave() {
    //     fetchAndSaveWelfareData().subscribe();
    // }
}
