package com.example.everguide.web.controller;

import com.example.everguide.api.ApiResponse;
import com.example.everguide.api.code.status.ErrorStatus;
import com.example.everguide.api.code.status.SuccessStatus;
import com.example.everguide.service.recommend.RecommendService;
import com.example.everguide.web.dto.recommend.RecommendRequest;
import com.example.everguide.web.dto.recommend.RecommendResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @Operation(summary = "설문 저장", description = "설문 결과를 저장합니다.")
    @PostMapping("/member/survey")
    public ResponseEntity<ApiResponse<String>> registerSurvey(
            @RequestBody RecommendRequest.SurveyDTO surveyDTO
    ) {

        try {
            if (recommendService.registerSurvey(surveyDTO)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.onSuccess(SuccessStatus._OK));

            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.onFailure(ErrorStatus._INTERNAL_SERVER_ERROR, "설문 저장에 실패했습니다."));
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST, e.getMessage()));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure(ErrorStatus._NOT_FOUND, "회원을 찾을 수 없습니다."));

        }
    }

    @Operation(summary = "정책 추천", description = "설문 기반의 정책 추천을 랜덤으로 6개 반환합니다.")
    @GetMapping("/member/recommend-welfare")
    public ResponseEntity<ApiResponse<List<RecommendResponse.RecommendDTO>>> welfareRecommend() {

        List<RecommendResponse.RecommendDTO> recommendDTOList = recommendService.welfareRecommend();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.onSuccess(SuccessStatus._OK, recommendDTOList));
    }

    @Operation(summary = "정책 추천 상세", description = "정책 추천의 상세 정보를 조회합니다.")
    @GetMapping("/member/recommend-welfare/{welfareId}")
    public ResponseEntity<ApiResponse<RecommendResponse.RecommendDetailsDTO>> welfareRecommendDetails(@PathVariable(name="welfareId") Long welfareId) {

        try {
            RecommendResponse.RecommendDetailsDTO recommendDetailsDTO = recommendService.welfareRecommendDetails(welfareId);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.onSuccess(SuccessStatus._OK, recommendDetailsDTO));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure(ErrorStatus._NOT_FOUND, "회원을 찾을 수 없습니다."));

        }
    }
}