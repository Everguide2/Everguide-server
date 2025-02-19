package com.example.everguide.service.education;


import com.example.everguide.domain.*;
import com.example.everguide.repository.BookmarkRepository;
import com.example.everguide.web.dto.education.EducationItemDetail;
import com.example.everguide.web.dto.education.EducationResponse;
import com.example.everguide.web.dto.education.EducationItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class EducationMappingService {
    private final BookmarkRepository bookmarkRepository;

    public static EducationResponse.GetEduDetailDto convertToDetailEduDetailDto(EducationItemDetail educationItem) {
        return EducationResponse.GetEduDetailDto.builder()
                .applyStartDate(educationItem.getAcptFrDd())
                .applyEndDate(educationItem.getAcptToDd())
                .region(educationItem.getCitiprovNo())
                .detailContent(educationItem.getCnts())
                .eduStartDate(educationItem.getLctreFrDd())
                .eduName(educationItem.getLctreNm())
                .eduHour(educationItem.getLctreTm())
                .eduEndDate(educationItem.getLctreToDd())
                .price(educationItem.getPartcptAmt())
                .howTo(educationItem.getReqMthd()).build();


    }

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
                                .educationId(education.getId())
                                .howTo(education.getHowTo())
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
    public EducationResponse.NoLoginSearchEduByNameListDto toNoLoginGetEduListSearchByName(Slice<Education> educations) {
        List<EducationResponse.SearchEduByNameDto> eduList = educations.stream()
                .map(this::toNoLoginEduDto)
                .collect(Collectors.toList());
        return EducationResponse.NoLoginSearchEduByNameListDto.builder()
                .searchEduByNameDtoList(eduList)
                .hasMore(educations.hasNext()).build();
    }
    public EducationResponse.SearchEduByNameDto toNoLoginEduDto(Education education) {
        return EducationResponse.SearchEduByNameDto.builder()
                .educationId(education.getId())
                .name(education.getEduName())
                .howTo(education.getHowTo())
                .dDay(calcDday(education.getEndDate())).build();

    }

    private static String calcDday(LocalDate endDate) {
        LocalDate today = LocalDate.now();
        long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(today, endDate);
        // 날짜 차이가 0 미만이면 -1 반환, 그 외에는 남은 일수를 반환
        return daysRemaining < 0 ? "-1" : String.valueOf(daysRemaining);
    }


    public EducationResponse.getRecommendEducationResultDto toGetRecommendEducationResultDto(List<Education> educationList, Member member) {
        List<EducationResponse.recommendEducationDetailDto> recommendEduList = educationList.stream()
                .map(education -> this.toRecommendEduDetail(education, member))
                .collect(Collectors.toList());

        return EducationResponse.getRecommendEducationResultDto.builder()
                .educationList(recommendEduList)
                .build();
    }

    private EducationResponse.recommendEducationDetailDto toRecommendEduDetail(Education education, Member member) {
        return EducationResponse.recommendEducationDetailDto.builder()
                .educationId(education.getId())
                .name(education.getEduName())
                .howTo(education.getHowTo())
                .dDay(calcDday(education.getEndDate()))
                .isBookMarked(bookmarkRepository.existsByEducationAndMember(education, member))
                .build();
    }


    //xml 결과를 엔티티 리스트로 변환
    public List<Education> convert(List<EducationItem> educationLists) {
        return educationLists.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
    }

    // xml item을 job 엔티티로 변환
    private Education mapToEntity(EducationItem dto) {
        return Education.builder()
                .educationKey1(dto.getEduCrseNo())
                .educationKey2(dto.getLctreNo())
                .startDate(stringToDate(dto.getLctreFrDd()))
                .endDate(stringToDate(dto.getLctreToDd()))
                .eduName(dto.getLctreNm())
                .howTo(dto.getReqMthd())
                .build();
    }
    private LocalDate stringToDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return LocalDate.parse(date, formatter);
    }
}
