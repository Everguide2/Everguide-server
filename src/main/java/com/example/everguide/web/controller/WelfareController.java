package com.example.everguide.web.controller;

import com.example.everguide.api.ApiResponse;
import com.example.everguide.api.code.status.SuccessStatus;
import com.example.everguide.domain.WelfareService;
import com.example.everguide.jwt.SecurityUtil;
import com.example.everguide.service.welfare.WelfareDataService;
import com.example.everguide.service.welfare.WelfareQueryService;
import com.example.everguide.web.dto.welfare.WelfareServiceDetailResponse;
import com.example.everguide.web.dto.welfare.WelfareServiceListResponse;
import com.example.everguide.web.dto.welfare.WelfareServiceSearchRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Tag(name = "Welfare", description = "복지 서비스 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/everguide")
public class WelfareController {

    private final WelfareDataService welfareDataService;
    private final WelfareQueryService welfareQueryService;
    private final SecurityUtil securityUtil;

    @Operation(summary = "복지 서비스 메인", description = "복지 서비스 메인 화면의 정책 목록을 조회합니다.")
    @GetMapping("/api/welfare/main")
    public ResponseEntity<ApiResponse<Page<WelfareServiceListResponse>>> getWelfareMain(
        @PageableDefault(size = 21, sort = "lastModYmd") Pageable pageable
    ) {
        Page<WelfareServiceListResponse> response = welfareQueryService.searchWelfareServices(
            new WelfareServiceSearchRequest("", List.of(), "", ""), 
            pageable
        );
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus.WELFARE_SEARCH_SUCCESS, response));
    }

    @Operation(summary = "복지 서비스 목록 검색", description = "복지 서비스 목록을 검색 조건에 따라 조회합니다.")
    @GetMapping("/api/welfare/search")
    public ResponseEntity<ApiResponse<Page<WelfareServiceListResponse>>> searchWelfareServices(
        WelfareServiceSearchRequest request,
        @PageableDefault(size = 21, sort = "lastModYmd") Pageable pageable
    ) {
        Page<WelfareServiceListResponse> response = welfareQueryService.searchWelfareServices(request, pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus.WELFARE_SEARCH_SUCCESS, response));
    }

    @Operation(summary = "복지 서비스 상세 조회", description = "복지 서비스의 상세 정보를 조회합니다. 로그인한 사용자의 경우 북마크 여부도 함께 조회됩니다.")
    @GetMapping("/api/welfare/{servId}")
    public ResponseEntity<ApiResponse<WelfareServiceDetailResponse>> getWelfareServiceDetail(
        @PathVariable String servId
    ) {
        // 로그인한 사용자인 경우 memberId 가져오기, 비로그인은 null
        Long memberId = null;
        try {
            String userId = securityUtil.getCurrentUserId();
            memberId = userId != null ? Long.parseLong(userId) : null;
        } catch (Exception e) {
            log.debug("비로그인 사용자의 복지 서비스 상세 조회");
        }

        WelfareServiceDetailResponse response = welfareQueryService.getWelfareServiceDetail(servId, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus.WELFARE_DETAIL_SUCCESS, response));
    }

    @Operation(summary = "복지 서비스 데이터 동기화", description = "외부 API에서 복지 서비스 데이터를 가져와 DB에 저장합니다.")
    @GetMapping("/api/welfare/sync")
    public Mono<ResponseEntity<ApiResponse<List<WelfareService>>>> syncWelfareData() {
        return welfareDataService.fetchAndSaveWelfareData()
                .map(entities -> ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus.WELFARE_SYNC_SUCCESS, entities)));
    }
}
