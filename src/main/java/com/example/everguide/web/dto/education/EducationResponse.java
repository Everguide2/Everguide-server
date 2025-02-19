package com.example.everguide.web.dto.education;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class EducationResponse {


    @Getter
    @Builder
    public static class getRecommendEducationResultDto {
        List<recommendEducationDetailDto> educationList;
    }
    @Getter
    @Builder
    public static class recommendEducationDetailDto {
        Long educationId;
        String name; // 교육이름
        String howTo;// 신청방법
        String dDay;
        Boolean isBookMarked;
    }

    @Getter
    @Builder
    public static class addEduBookmarkResultDto {
        Long memberId;
        Long educationId;
        String bookmarkType;

    }

    @Getter
    @Builder
    public static class deleteEduBookmarkResultDto {
        Long educationId; //삭제된 교육 아이디

    }


    @Getter
    @Builder
    public static class NoLoginSearchEduByNameListDto {
        List<SearchEduByNameDto> searchEduByNameDtoList;
        Boolean hasMore; //다음 페이지 여부
    }


    @Getter
    @Builder
    public static class SearchEduByNameDto {
        Long educationId;
        String name; // 교육이름
        String howTo;// 신청방법
        String dDay;
    }

    @Getter
    @Builder
    public static class GetWorthToGoListDto {
        List<GetWorthToGoDto> educationList;
        Boolean hasMore;

    }


    @Getter
    @Builder
    public static class GetWorthToGoDto {
        Long educationId;
        String startDate;// 강좌 시작일
        String endDate;// 강좌 종료일
        String eduName;// 강좌명
        String howTo;// 신청방법
    }


    @Getter
    @Builder
    public static class GetEduDetailDto {
        String applyStartDate; //접수 시작일	acptFrDd
        String applyEndDate; //접수 종료일	acptToDd
        String region;//지역	citiprovNo
        String detailContent; //상세내용	cnts
        String eduStartDate; //강좌시작일	lctreFrDd
        String eduName; //강좌명	lctreNm
        String eduHour;//교육시간	lctreTm
        String eduEndDate;//강좌종료일	lctreToDd
        String price;//참가비	partcptAmt
        String howTo;//신청방법	reqMthd

    }
}
