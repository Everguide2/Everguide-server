package com.example.everguide.web.dto.welfare;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

public class WelfareServiceRequest {

    @Builder
    public record WelfareServiceListRequest(
        @Schema(description = "페이지 번호", defaultValue = "0")
        @Min(0) Integer page,
        
        @Schema(description = "페이지 크기", defaultValue = "10")
        @Min(1) @Max(100) Integer size,
        
        @Schema(description = "시도명")
        String ctpvNm,
        
        @Schema(description = "시군구명")
        String sggNm,
        
        @Schema(description = "관심주제")
        String intrsThemaNm,
        
        @Schema(description = "생애주기")
        String lifeNm,
        
        @Schema(description = "가구상황")
        String trgterIndvdlNm,
        
        @Schema(description = "정렬 기준(LATEST, VIEWS)", defaultValue = "LATEST")
        String sortBy
    ) {}
}