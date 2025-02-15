package com.example.everguide.web.dto.job;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class JobResponse {
    @Getter
    @Builder
    public static class GetJobList {
        Integer count; //일자리 수
        List<JobDto> jobDtoList;
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

    }
}
