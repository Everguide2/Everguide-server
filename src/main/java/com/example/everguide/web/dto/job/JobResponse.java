package com.example.everguide.web.dto.job;

import com.example.everguide.domain.enums.Region;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class JobResponse {

    @Getter
    @Builder
    public static class addJobBookmarkResultDto {
        Long memberId;
        Long jobId;
        String bookmarkType;

    }

    @Getter
    @Builder
    public static class deleteJobBookmarkResultDto {
        Long jobId; //삭제된 일자리 아이디

    }


    @Getter
    @Builder
    public static class GetJobList {
        Integer count; //일자리 수
        List<JobDto> jobDtoList;
        List<Region> regionList;
        Boolean isRecruiting;
        String sortBy; // 정렬 조건
    }
    @Getter
    @Builder
    public static class JobDto {
        Long jobId; //아이디값
        String jobName;//일자리 이름
        String dDay; // //디데이
        String startDate; //시작일
        String endDate; //마감일
        String hireType; // 마감 여부
        String company; //담당 회사
        String region; //지역
        String regionDetail; //상세 지역
        Boolean isBookmarked; //북마크 여부
    }
    //일자리 상세정보 요청 응답
    @Getter
    @Builder
    public static class GetJobDetailDto {
        String hireType; //접수 여부 HIRETYPE
        String company; //사업장명 plbizNm
        String jobName; //체용 제목 wantedTitle
        String startDate; //시작 접수일
        String endDate;//종료 접수일
        String address;//주소 plDetAddr
        String detailContext;//상세네용 detCnts
        String etc;//기타사항 etcItm
        String recruitNum;//모집인원 clltPrnnum
        String age;//연령 age
        String apply;//접수방법 acptMthdCd
        String clerk;//담당자 clerk
        String clerkContect;//담당자 연락처 clerkContt
        String homepage;//홈페이지 페이지 homepage

    }
    @Getter
    @Builder
    public static class ThisWeekJobsDto {
        //대표 일자리
        ThisWeekBigJob bigJob;

        List<ThisWeekSmallJob> smallJobList;
       //기업명 , 채용 제목 - 근무 지역

    }
    @Getter
    @Builder
    public static class ThisWeekSmallJob {
        Long jobId;//직업 아이디 (상세 정보 조회 아이디)
        String companyName;//기업명
        String jobName;// 채용 제목
        String region;// 지역 (경기도)
        String regionDetail; // 상세 지역 (아산시)
    }
    @Getter
    @Builder
    public static class ThisWeekBigJob {
        Long jobId;//직업 아이디 (상세 정보 조회 아이디)
        String companyName;//기업명
        String jobName;// 채용 제목
        String region;// 지역 (경기도)
        String regionDetail; // 상세 지역 (아산시)
        String startDate; //시작일
        String endDate; //마감일
        String hireType; //접수 여부
    }


    @Getter
    @Builder
    public static class GetJobListSearchByName {
        List<JobDto> jobDtoList; //일자리 리스트 정보
        Boolean hasMore; //다음 페이지 여부
    }


    @Getter
    @Builder
    public static class GetJobCountByRegionDto {
        Integer all;
        Integer seoul;
        Integer incheon;
        Integer busan;
        Integer gyeonggido;
        Integer chungcheongnamdo;
        Integer chungcheongbukdo;
        Integer jeollanamdo;
        Integer jeollabukdo;
        Integer gyeongsangbukdo;
        Integer gyeongsangnamdo;
        Integer gangwondo;
        Integer jejudo;


    }
}
