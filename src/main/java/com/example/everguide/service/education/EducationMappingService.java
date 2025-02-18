package com.example.everguide.service.education;


import com.example.everguide.domain.Education;
import com.example.everguide.web.dto.education.EducationResponse;
import org.springframework.data.domain.Slice;

import java.util.List;

public class EducationMappingService {
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

}
