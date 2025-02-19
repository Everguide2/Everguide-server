package com.example.everguide.web.dto.recommend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class RecommendResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendDTO {

        Long id;
        String servId;
        String servNm; // 서비스명
        String aplyMtdNm; // 신청방법명
        String bizChrDeptNm; // 사업담당부서명
        String sprtCycNm; // 지원주기명
        String srvPvsnNm; // 제공유형명
    }
}