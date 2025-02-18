package com.example.everguide.web.dto.education;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class EducationResponse {


    @Getter
    @Builder
    public static class GetWorthToGoListDto {
        List<GetWorthToGoDto> educationList;
        Boolean hasMore;

    }


    @Getter
    @Builder
    public static class GetWorthToGoDto {
        String startDate;// 강좌 시작일
        String endDate;// 강좌 종료일
        String eduName;// 강좌명
        String CompanyName;// 기관명 <- 더미데이터 기준, 실제이면 신청방법 들어갈 예정
    }
}
