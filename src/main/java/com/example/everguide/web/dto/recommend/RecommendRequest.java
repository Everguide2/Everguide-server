package com.example.everguide.web.dto.recommend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class RecommendRequest {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SurveyDTO {

        String surveyTarget;
        String region;
        List<String> supportTypes;
        List<String> householdTypes;
        String disabilityGrade;
    }
}