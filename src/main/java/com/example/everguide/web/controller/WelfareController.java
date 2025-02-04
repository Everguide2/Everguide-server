package com.example.server.controller;

import com.example.server.api.ApiResponse;
import com.example.server.api.code.status.SuccessStatus;
import com.example.server.entity.WelfareServiceEntity;
import com.example.server.service.WelfareDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WelfareController {

    private final WelfareDataService welfareDataService;

    // 외부 API 호출 후 DB 저장된 복지 서비스 데이터를 조회하여 통일된 응답 포맷(ApiResponse)으로 반환합니다.
    @GetMapping("/api/welfare")
    public Mono<ResponseEntity<ApiResponse<List<WelfareServiceEntity>>>> getWelfareData() {
        return welfareDataService.fetchAndSaveWelfareData()
                .map(entities -> ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, entities)));
    }
}
