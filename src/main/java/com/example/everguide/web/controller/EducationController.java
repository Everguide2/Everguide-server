package com.example.everguide.web.controller;


import com.example.everguide.api.ApiResponse;
import com.example.everguide.api.code.status.SuccessStatus;
import com.example.everguide.service.education.EducationMappingService;
import com.example.everguide.service.education.EducationService;
import com.example.everguide.service.job.JobMappingService;
import com.example.everguide.web.dto.education.EducationRequest;
import com.example.everguide.web.dto.education.EducationResponse;
import com.example.everguide.web.dto.job.JobRequest;
import com.example.everguide.web.dto.job.JobResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;
    // 로그인 없어도 접근 가능한 컨트롤러
    @GetMapping("/educations/getWorthToGo")
    public ResponseEntity<ApiResponse<EducationResponse.GetWorthToGoListDto>> getThisWeekJob(@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                                             @RequestParam(value = "size", required = false, defaultValue = "4") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, EducationMappingService.toGetWorthToGoResultDto(educationService.getWorthToGoList(pageable))));

    }

    @GetMapping("/educations/searchEduByName")
    public ResponseEntity<ApiResponse<EducationResponse.NoLoginSearchEduByNameListDto>> searchEduByName(@RequestParam(value = "name") String name,
                                                                                                        @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                                                        @RequestParam(value = "size", required = false, defaultValue = "4") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, educationService.noLoginSearchEduListByName(name, pageable)));

    }



    //로그인이 필요한 컨트롤러-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    @PostMapping("member/educations/addEduBookmark")
    public ResponseEntity<ApiResponse<EducationResponse.addEduBookmarkResultDto>> addEduBookmark(@ModelAttribute EducationRequest.addEduBookmarkDto request) {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, EducationMappingService.toAddEduBookMarkResponseResultDto(educationService.addEduBookmark(request))));
    }

    @DeleteMapping("member/educations/deleteEduBookmark")
    public ResponseEntity<ApiResponse<EducationResponse.deleteEduBookmarkResultDto>> deleteEduBookMark(@ModelAttribute EducationRequest.deleteEduBookmarkDto request) {
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, EducationMappingService.toDeleteEduBookmarkResultDto(educationService.deleteJobBookmark(request))));
    }

}
