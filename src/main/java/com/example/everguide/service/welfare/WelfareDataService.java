package com.example.everguide.service.welfare;

import com.example.everguide.domain.WelfareService;
import com.example.everguide.repository.WelfareServiceRepository;
import com.example.everguide.web.dto.welfare.WantedListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WelfareDataService {

    private final WebClient webClient;
    private final WelfareMappingService welfareMappingService;
    private final WelfareServiceRepository repository;

   @Value("${api.service.key}")
   private String serviceKey;

    private static final String API_ENDPOINT = "/LcgvWelfarelist";

    // 외부 API 호출 → DTO 매핑 → Entity 변환 및 DB 저장 과정을 수행
    public Mono<List<WelfareService>> fetchAndSaveWelfareData() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_ENDPOINT)
//                        .queryParam("serviceKey", serviceKey)
//                        .queryParam("pageNo", 1)
//                        .queryParam("numOfRows", 10)
                        .build())
                .accept(MediaType.APPLICATION_XML)
                .retrieve()
                .bodyToMono(WantedListResponse.class)
                .flatMap(response -> {
                    if ("0".equals(response.getResultCode())) {
                        List<WelfareService> entities = welfareMappingService.convert(response.getServList());
                        repository.saveAll(entities);
                        return Mono.just(entities);
                    } else {
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
