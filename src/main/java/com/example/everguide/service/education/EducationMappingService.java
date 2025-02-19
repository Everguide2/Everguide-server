package com.example.everguide.service.education;


import com.example.everguide.domain.Bookmark;
import com.example.everguide.domain.Education;
import com.example.everguide.web.dto.education.EducationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class EducationMappingService {


    public static EducationResponse.deleteEduBookmarkResultDto toDeleteEduBookmarkResultDto(Long educationId) {
        return EducationResponse.deleteEduBookmarkResultDto.builder()
                .educationId(educationId)
                .build();
    }

    public static EducationResponse.addEduBookmarkResultDto toAddEduBookMarkResponseResultDto(Bookmark bookmark) {
        return EducationResponse.addEduBookmarkResultDto.builder()
                .memberId(bookmark.getMember().getId())
                .educationId(bookmark.getEducation().getId())
                .bookmarkType(bookmark.getType().name())
                .build();
    }



    public static EducationResponse.GetWorthToGoListDto toGetWorthToGoResultDto(Slice<Education> educations) {
        List<EducationResponse.GetWorthToGoDto> educationList = educations.stream()
                .map(education ->
                        EducationResponse.GetWorthToGoDto.builder()
                                .CompanyName(education.getCompanyName())
                                .eduName(education.getEduName())
                                .startDate(String.valueOf(education.getStartDate()))
                                .endDate(String.valueOf(education.getEndDate()))
                                .build()).toList();
        return EducationResponse.GetWorthToGoListDto.builder()
                .educationList(educationList)
                .hasMore(educations.hasNext()). //다음페이지 존재 여부
                build();


    }
    //로그인 안했을 때, 검색결과
    public EducationResponse.NoLoginSearchEduByNameListDto toNoLoginGetJobListSearchByName(Slice<Education> educations) {
        List<EducationResponse.SearchEduByNameDto> eduList = educations.stream()
                .map(this::toNoLoginEduDto)
                .collect(Collectors.toList());
        return EducationResponse.NoLoginSearchEduByNameListDto.builder()
                .searchEduByNameDtoList(eduList)
                .hasMore(educations.hasNext()).build();
    }
    public EducationResponse.SearchEduByNameDto toNoLoginEduDto(Education education) {
        return EducationResponse.SearchEduByNameDto.builder()
                .name(education.getEduName())
                .companyName(education.getCompanyName())
                .dDay(calcDday(education.getEndDate())).build();

    }

    private static String calcDday(LocalDate endDate) {
        LocalDate today = LocalDate.now();
        long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(today, endDate);
        // 날짜 차이가 0 미만이면 -1 반환, 그 외에는 남은 일수를 반환
        return daysRemaining < 0 ? "-1" : String.valueOf(daysRemaining);
    }

}
