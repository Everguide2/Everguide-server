package com.example.everguide.web.controller;


import com.example.everguide.api.ApiResponse;
import com.example.everguide.api.code.status.SuccessStatus;
import com.example.everguide.domain.Education;
import com.example.everguide.service.education.EducationDataService;
import com.example.everguide.service.education.EducationMappingService;
import com.example.everguide.service.education.EducationService;
import com.example.everguide.web.dto.education.EducationRequest;
import com.example.everguide.web.dto.education.EducationResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EducationController {

    private final EducationDataService educationDataService;
    private final EducationService educationService;

    // 로그인 없어도 접근 가능한 컨트롤러
    @Operation(summary = "기본 교육 상세정보 조회", description = "교육의 상세정보를 조회합니다.")
    @GetMapping("/educations/{educationId}")
    public Mono<ResponseEntity<ApiResponse<EducationResponse.GetEduDetailDto>>> getEducationDetail(@PathVariable Long educationId) {
        return educationDataService.fetchEducationDetailData(educationId)
                .map(entity -> ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, entity)));
    }

    @Operation(summary = "교육 리스트 정보 db 적재", description = "교육 리스트 정보를 db에 적재합니다..")
    @GetMapping("/api/education")
    public Mono<ResponseEntity<ApiResponse<List<Education>>>> getEducationData() {
        return educationDataService.fetchAndSaveEducationData()
                .map(entities -> ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, entities)));
    }

    @Operation(summary = "홈화면 참여할만한 교육 리스트 조회", description = "홈화면의 참여할만한 교육 리스트를 조회합니다.")
    @GetMapping("/educations/getWorthToGo")
    public ResponseEntity<ApiResponse<EducationResponse.GetWorthToGoListDto>> getWorthToGo(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                                             @RequestParam(value = "size", required = false, defaultValue = "4") Integer size) {
        Pageable pageable = PageRequest.of(page-1, size);
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, EducationMappingService.toGetWorthToGoResultDto(educationService.getWorthToGoList(pageable),pageable.getPageNumber())));

    }
    @Operation(summary = "기본 교육 이름 검색", description = "교육 정보를 이름으로 검색합니다. 검색 키워드를 입력 받습니다.")
    @GetMapping("/educations/searchEduByName")
    public ResponseEntity<ApiResponse<EducationResponse.SearchEduByNameListDto>> noLoginSearchEduByName(@RequestParam(value = "name") String name,
                                                                                                        @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                                                        @RequestParam(value = "size", required = false, defaultValue = "4") Integer size) {
        Pageable pageable = PageRequest.of(page-1, size);
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, educationService.noLoginSearchEduListByName(name, pageable)));
    }
    @Operation(summary = "기본 교육 리스트 조회", description = "교육 리스트를 조회합니다. 마감기간(7days, 30days, over30days, always, closed), 페이지, 페이지의 데이터수, 검색 키워드를 입력받습니다.")
    @GetMapping("educations/getEducationList")
    public ResponseEntity<ApiResponse<EducationResponse.GetEduCationListDto>> noLoginGetEducationList(
                                                                @RequestParam(value = "deadline", required = false) List<String> deadline,
                                                                @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                @RequestParam(value = "size", required = false, defaultValue = "21") Integer size,
                                                                @RequestParam(value = "keyWord", required = false) String keyWord) {
        Pageable pageable = PageRequest.of(page - 1, size);

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, educationService.noLoginGetEducationList(deadline, pageable, keyWord)));

    }
    @Operation(summary = "교육 리스트 조회 시, 마감기간 개수 값 조회", description = "교육 리스트 조회시, 마감 기간 옆에 사용하는 개수 값을 조회합니다.")
    @GetMapping("educations/getEndDateCount")
    public ResponseEntity<ApiResponse<EducationResponse.getEndDateCount>> getEndDateCount() {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, educationService.getEndDateCount()));

    }



    //로그인이 필요한 컨트롤러-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    @Operation(summary = "로그인한 회원 교육 북마크 추가", description = "로그인한 회원이 해당 교육을 북마크에 추가합니다.")
    @PostMapping("member/educations/addEduBookmark")
    public ResponseEntity<ApiResponse<EducationResponse.addEduBookmarkResultDto>> addEduBookmark(@ModelAttribute EducationRequest.addEduBookmarkDto request) {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, EducationMappingService.toAddEduBookMarkResponseResultDto(educationService.addEduBookmark(request))));
    }

    @Operation(summary = "로그인한 회원 교육 북마크 삭제", description = "로그인한 회원이 해당 교육을 북마크 삭제합니다.")
    @DeleteMapping("member/educations/deleteEduBookmark")
    public ResponseEntity<ApiResponse<EducationResponse.deleteEduBookmarkResultDto>> deleteEduBookMark(@ModelAttribute EducationRequest.deleteEduBookmarkDto request) {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, EducationMappingService.toDeleteEduBookmarkResultDto(educationService.deleteEduBookmark(request))));
    }

    @Operation(summary = "로그인한 회원  교육 추천 조회", description = "로그인한 회원이 추천 교육을 조회받습니다. ")
    @GetMapping("member/educations/getRecommendEducation")
    public ResponseEntity<ApiResponse<EducationResponse.getRecommendEducationResultDto>> getRecommendEdu() {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, educationService.getRandom6Edu()));
    }
    @Operation(summary = "로그인한 회원 교육 북마크 여부 조회", description = "로그인한 회원이 해당 교육의 북마크 여부를 조회합니다.")
    @GetMapping("member/educations/isBookMarked/{educationId}")
    public ResponseEntity<ApiResponse<Boolean>> isBookMarked(@RequestParam("educationId") Long educationId) {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, educationService.isBookMarked(educationId)));
    }

    @Operation(summary = "로그인한 회원 교육 리스트 조회", description = "로그인한 회원이 교육 리스트를 조회합니다. 마감기간(7days, 30days, over30days, always, closed), 페이지, 페이지의 데이터수, 검색 키워드를 입력받습니다. 북마크 여부를 추가로 반환합니다.")
    @GetMapping("member/educations/getEducationList")
    public ResponseEntity<ApiResponse<EducationResponse.GetEduCationListDto>> loginGetEducationList(
            @RequestParam(value = "deadline", required = false) List<String> deadline,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "21") Integer size,
            @RequestParam(value = "keyWord", required = false) String keyWord) {
        Pageable pageable = PageRequest.of(page - 1, size);

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, educationService.getEducationList(deadline, pageable, keyWord)));

    }
    @Operation(summary = "로그인한 회원  교육 이름 검색", description = "로그인한 회원이 교육을 이름으로 검색합니다. 북마크 여부를 추가로 반환합니다.")
    @GetMapping("member/educations/searchEduByName")
    public ResponseEntity<ApiResponse<EducationResponse.SearchEduByNameListDto>> searchEduByName(@RequestParam(value = "name") String name,
                                                                                                 @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                                                 @RequestParam(value = "size", required = false, defaultValue = "4") Integer size) {
        Pageable pageable = PageRequest.of(page-1, size);
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, educationService.searchEduListByName(name, pageable)));

    }
}
