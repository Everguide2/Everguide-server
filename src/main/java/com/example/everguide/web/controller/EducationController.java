package com.example.everguide.web.controller;


import com.example.everguide.api.ApiResponse;
import com.example.everguide.api.code.status.SuccessStatus;
import com.example.everguide.service.education.EducationMappingService;
import com.example.everguide.service.education.EducationService;
import com.example.everguide.web.dto.education.EducationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;
    // 로그인 없어도 접근 가능한 컨트롤러
    @GetMapping("educations/getWorthToGo")
    public ResponseEntity<ApiResponse<EducationResponse.GetWorthToGoListDto>> getThisWeekJob(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                                             @RequestParam(value = "size", required = false, defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, EducationMappingService.toGetWorthToGoResultDto(educationService.getWorthToGoList(pageable))));

    }





    //----------------------------------------------------------------------------------------------------------------------------------------------// 로그인 했을 때 접근 가능한 컨트롤러
    }
