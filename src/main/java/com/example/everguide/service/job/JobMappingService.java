package com.example.everguide.service.job;

import com.example.everguide.domain.Bookmark;
import com.example.everguide.domain.Job;
import com.example.everguide.domain.enums.HireType;
import com.example.everguide.domain.enums.Region;
import com.example.everguide.domain.enums.RegionDetail;
import com.example.everguide.web.dto.job.JobItem;
import com.example.everguide.web.dto.job.JobItemDetail;
import com.example.everguide.web.dto.job.JobResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobMappingService {

    public static JobResponse.deleteJobBookmarkResultDto toDeleteJobBookmarkResultDto(Long jobId) {
        return JobResponse.deleteJobBookmarkResultDto.builder()
                .jobId(jobId).build();
    }

    public static JobResponse.addJobBookmarkResultDto toAddJobBookMarkResponseResultDto(Bookmark bookmark) {
        return JobResponse.addJobBookmarkResultDto.builder()
                .memberId(bookmark.getMember().getId())
                .jobId(bookmark.getJob().getId())
                .bookmarkType(bookmark.getType().name())
                .build();
    }

    public static JobResponse.ThisWeekJobsDto toThisWeekJobsDto(List<Job> jobs) {
        return JobResponse.ThisWeekJobsDto.builder()
                .bigJob(toBigJobDto(jobs.get(0)))
                .smallJobList(toSmalljobListDto(jobs.subList(1, jobs.size())))
                .build();

    }

    private static List<JobResponse.ThisWeekSmallJob> toSmalljobListDto(List<Job> jobs) {
        return jobs.stream().map(j ->
                JobResponse.ThisWeekSmallJob.builder()
                        .jobId(j.getId())
                        .companyName(j.getOrganName())
                        .jobName(j.getName())
                        .region(j.getRegion()!= null ? j.getRegion().getDescription() : "")
                        .regionDetail(j.getRegionDetail() != null ? j.getRegionDetail().getRegionDetail() : "").build()).toList();
    }

    private static JobResponse.ThisWeekBigJob toBigJobDto(Job job) {
        return JobResponse.ThisWeekBigJob.builder()
                .jobId(job.getId())
                .companyName(job.getOrganName())
                .jobName(job.getName())
                .region(job.getRegion() != null ? job.getRegion().getDescription() : "")
                .regionDetail(job.getRegionDetail() != null ? job.getRegionDetail().getRegionDetail() : "")
                .startDate(String.valueOf(job.getStartDate()))
                .endDate(String.valueOf(job.getEndDate()))
                .hireType(job.getHireType() != null ? job.getHireType().name() : "")
                .build();

    }

    //
    //xml 데이터를 받아, response 객체로 변환
    public static JobResponse.GetJobDetailDto convertToDetailResponse(JobItemDetail jobItemDetail, Job job) {
        return JobResponse.GetJobDetailDto.builder()
                .hireType(job.getHireType().name())
                .company(jobItemDetail.getPlbizNm())
                .jobName(jobItemDetail.getWantedTitle())
                .startDate(String.valueOf(job.getStartDate()))
                .endDate(String.valueOf(job.getEndDate()))
                .address(jobItemDetail.getPlDetAddr())
                .detailContext(jobItemDetail.getDetCnts())
                .etc(jobItemDetail.getEtcItm())
                .recruitNum(jobItemDetail.getClltPrnnum())
                .age(jobItemDetail.getAge())
                .apply(jobItemDetail.getAcptMthdCd())
                .clerk(jobItemDetail.getClerk())
                .clerkContect(jobItemDetail.getClerkContt())
                .homepage(jobItemDetail.getHomepage())
                .build();

    }


    public static JobResponse.GetJobList toJobListDto(List<Job> jobs) {
        List<JobResponse.JobDto> jobList = jobs.stream().map(JobMappingService::toJobDto).collect(Collectors.toList());
        return JobResponse.GetJobList.builder()
                .jobDtoList(jobList)
                .count(jobList.size())
                .build();
    }


    public static JobResponse.JobDto toJobDto(Job job) {
        return JobResponse.JobDto.builder()
                .jobId(job.getId())
                .jobName(job.getName())
                .dDay(calcDday(job.getEndDate()))
                .startDate(String.valueOf(job.getStartDate()))
                .endDate(String.valueOf(job.getEndDate()))
                .hireType(job.getHireType().name())
                .company(job.getOrganName())
                .region(job.getRegion().getDescription())
                .regionDetail(job.getRegionDetail().getRegionDetail())
                .build();
    }

    private static String calcDday(LocalDate endDate) {
        LocalDate today = LocalDate.now();
        long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(today, endDate);
        // 날짜 차이가 0 미만이면 -1 반환, 그 외에는 남은 일수를 반환
        return daysRemaining < 0 ? "-1" : String.valueOf(daysRemaining);
    }

    //xml 결과를 엔티티 리스트로 변환
    public List<Job> convert(List<JobItem> jobLists) {
        return jobLists.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
    }

    // xml item을 job 엔티티로 변환
    private Job mapToEntity(JobItem dto) {
        Job entity = new Job();
        return Job.builder()
                .jobCode(dto.getJobId())
                .organName(dto.getOranNm()) //기업명
                .hireType(HireType.toHireType(dto.getDeadline())) //마감여부
                .startDate(stringToDate(dto.getFrDd())) //시작일
                .endDate(stringToDate(dto.getToDd())) // 마감일
                .name(dto.getRecrtTitle()) //채용제목
                .region(classifyRegion(dto.getWorkPlc())) //지역
                .regionDetail(RegionDetail.classifyRegionDetail(dto.getWorkPlc())) //상세 지역
                .build();

    }

    private LocalDate stringToDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return LocalDate.parse(date, formatter);
    }


    //지역코드
    public static Region classifyRegion(String code) {
        // 서울특별시 (010000 ~ 010250)
        if (code.startsWith("010")) {
            return Region.SEOUL;
        }
        // 인천광역시 (110000 ~ 110100)
        else if (code.startsWith("110")) {
            return Region.INCHEON;
        }
        // 부산광역시 (090000 ~ 090150)
        else if (code.startsWith("090")) {
            return Region.BUSAN;
        }
        // 경기도 (030000 ~ 030530)
        else if (code.startsWith("030")) {
            return Region.GYEONGGI;
        }
        // 충청남도 (150000 ~ 150110)
        else if (code.startsWith("150")) {
            return Region.CHUNGNAM;
        }
        // 충청북도 (160000 ~ 160140)
        else if (code.startsWith("160")) {
            return Region.CHUNGBUK;
        }
        // 전라남도 (120000 ~ 120190)
        else if (code.startsWith("120")) {
            return Region.JEONNAM;
        }
        // 전라북도 (130000 ~ 130160)
        else if (code.startsWith("130")) {
            return Region.JEONBUK;
        }
        // 경상북도 (050000 ~ 050250)
        else if (code.startsWith("050")) {
            return Region.GYEONGBUK;
        }
        // 경상남도 (040000 ~ 040180)
        else if (code.startsWith("040")) {
            return Region.GYEONGNAM;
        }
        // 강원도 (020000 ~ 020180)
        else if (code.startsWith("020")) {
            return Region.GANGWON;
        }
        // 제주특별자치도 (140000 ~ 140010)
        else if (code.startsWith("140")) {
            return Region.JEJU;
        }
        // 세종특별자치시 (170000)
        else if (code.startsWith("170")) {
            return Region.SEJONG;
        }

        // 기본적으로 알 수 없는 경우는 null 반환
        return null;
    }
}
