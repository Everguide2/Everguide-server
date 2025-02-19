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
        String serviceId;
        String serviceName; // 서비스명
        String applyMethod; // 신청방법명
        String chargeDepartment; // 사업담당부서명
        String supportCycle; // 지원주기명
        String provisionType; // 제공유형명
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendDetailsDTO {

        Long id;
        String serviceId;
        String serviceName;
        String serviceDigest;
        String serviceDetailLink;
        String applyMethod;
        String chargeDepartment;
        String region;
        String supportTypes;
        String supportCycle;
        String provisionType;
        String householdConditions;
        String regionDetail;
    }
}