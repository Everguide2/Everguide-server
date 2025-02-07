package com.example.everguide.service.job;

import com.example.everguide.domain.Job;
import com.example.everguide.domain.enums.HireType;
import com.example.everguide.domain.enums.JobType;
import com.example.everguide.web.dto.job.JobItem;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobMappingService {

    //xml 결과를 엔티티 리스트로 변환
    public List<Job> convert(List<JobItem> jobLists) {
        return jobLists.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
    }

    // xml item을 job 엔티티로 변환
    private Job mapToEntity(JobItem dto) {
        Job entity = new Job();
        return entity.builder()
                .jobCode(dto.getJobCode())
                .companyName(dto.getCompanyName())
                .projectYear(dto.getProjectYear())
                .regionSido(dto.getRegionSido())
                .regionSigungu(dto.getRegionSigungu())
                .wage(dto.getWage())
                .jobType(JobType.toJobType(dto.getJobType()))
                .workPlace(dto.getWorkPlace())
                .recruitCnt(dto.getRecruitCnt())
                .hireType(HireType.toHireType(dto.getHireType()))
                .postingEndDate(stringToDate(dto.getPostingStartDate()))
                .postingStartDate(stringToDate(dto.getPostingStartDate()))
                .build();
    }

    private LocalDate stringToDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return LocalDate.parse(date, formatter);
    }
}
