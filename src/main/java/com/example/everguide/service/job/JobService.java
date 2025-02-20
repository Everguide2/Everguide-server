package com.example.everguide.service.job;

import com.example.everguide.api.code.status.ErrorStatus;
import com.example.everguide.api.exception.GeneralException;
import com.example.everguide.domain.Bookmark;
import com.example.everguide.domain.Job;
import com.example.everguide.domain.Member;
import com.example.everguide.domain.enums.BookmarkType;
import com.example.everguide.domain.enums.Region;
import com.example.everguide.jwt.SecurityUtil;
import com.example.everguide.repository.BookmarkRepository;
import com.example.everguide.repository.JobCountProjection;
import com.example.everguide.repository.JobRepository;
import com.example.everguide.repository.MemberRepository;
import com.example.everguide.web.dto.job.JobRequest;
import com.example.everguide.web.dto.job.JobResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepository;
    private final MemberRepository memberRepository;
    private final BookmarkRepository bookmarkRepository;
    private final JobMappingService jobMappingService;
    private final SecurityUtil securityUtil;


    @Transactional(readOnly = true)
    public Integer getTotalCountSearchByName(String name) {
        return jobRepository.countByNameContainingIgnoreCase(name);


    }

    @Transactional(readOnly = true)
    public Boolean isBookmarked(Long JobId) {
        String userId = securityUtil.getCurrentUserId();
        Member member = memberRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        Job job = jobRepository.findById(JobId).orElseThrow(() -> new GeneralException(ErrorStatus._JOB_NOT_FOUND));
        return bookmarkRepository.existsByJobAndMember(job, member);
    }


    @Transactional(readOnly = true)
    public JobResponse.GetJobList noLoginGetJobListResult(List<Region> regionList, String sortBy, Boolean isRecruiting, Pageable pageable) {
        List<Job> jobs = jobRepository.noLoginFindJobList(regionList, sortBy, isRecruiting, pageable);
        return jobMappingService.toNoLoginJobListDto(jobs,  regionList, sortBy, isRecruiting);
    }


    @Transactional(readOnly = true)
    public JobResponse.GetJobList getJobListResult(List<Region> regionList, String sortBy, Boolean isRecruiting, Pageable pageable) {

        String userId = securityUtil.getCurrentUserId();
        Member member = memberRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);

        List<Job> jobList = jobRepository.findJobList(regionList, sortBy, isRecruiting, pageable, member);
        return jobMappingService.toJobListDto(jobList, member, regionList, sortBy, isRecruiting);
    }



    @Transactional(readOnly = true)
    public Job getJobById(Long id) {
        return jobRepository.findById(id).orElseThrow(() -> new GeneralException(ErrorStatus._JOB_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Job> getThisWeekJobList() throws GeneralException {
        List<Job> thisWeekJobList = jobRepository.findThisWeekJobList(); //상위 7개 조회
        if (!thisWeekJobList.isEmpty()) {
            return thisWeekJobList;
        } else {
            throw new GeneralException(ErrorStatus._THIS_WEEK_JOB_NOT_FOUND);
        }
    }

    @Transactional
    public Bookmark addJobBookmark(JobRequest.addJobBookmarkDto request) {
        // 회원 조회 -> 여기서 추후, 멤버 로그인 아이디로 조회해야 한다 -> 혜원님 푸시 이후 개발 하는 걸로
        String userId = securityUtil.getCurrentUserId();
        Member member = memberRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        // 일자리 조회
        Job job = jobRepository.findById(request.getJobId()).orElseThrow(() -> new GeneralException(ErrorStatus._JOB_NOT_FOUND));
        // 이미 존재하는 북마크인지 확인
        if (bookmarkRepository.findByMemberAndJob(member, job).isPresent()) {
            throw new GeneralException(ErrorStatus._JOB_BOOKMARK_ALREADY_EXIST);
        }
        // 북마크 생성
        Bookmark bookmark = new Bookmark();
        bookmark.setType(BookmarkType.JOB);
        bookmark.setMember(member);
        bookmark.setJob(job);
        //북마크 저장
        return bookmarkRepository.save(bookmark);

    }

    //로그인 안했을 때, 검색 기능
    @Transactional(readOnly = true)
    public JobResponse.GetJobListSearchByName noLoginSearchJobListByName(String keyword, Pageable pageable) {
        return jobMappingService.toNoLoginGetJobListSearchByName(jobRepository.searchJobListByName(keyword, pageable));
    }


    //로그인 했을 때, 검색 기능
    @Transactional(readOnly = true)
    public JobResponse.GetJobListSearchByName SearchJobListByName(String keyword, Pageable pageable) {
        String userId = securityUtil.getCurrentUserId();
        Member member = memberRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        return jobMappingService.toGetJobListSearchByName(jobRepository.searchJobListByName(keyword, pageable), member);
    }


    //일자리 북마크
    @Transactional
    public Long deleteJobBookmark(JobRequest.deleteJobBookmarkDto request) {
        // 회원 조회 -> 여기서 추후, 멤버 로그인 아이디로 조회해야 한다 -> 혜원님 푸시 이후 개발 하는 걸로

        String userId = securityUtil.getCurrentUserId();
        Member member = memberRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        // 일자리 조회
        Job job = jobRepository.findById(request.getJobId()).orElseThrow(() -> new GeneralException(ErrorStatus._JOB_NOT_FOUND));
        // 북마크가 DB에 존재하는 지 확인
        Bookmark bookmark = bookmarkRepository.findByMemberAndJob(member, job).orElseThrow(() -> new GeneralException(ErrorStatus._JOB_BOOKMARK_NOT_FOUND));
        // 북마크 삭제 진행
        bookmarkRepository.deleteById(bookmark.getId());
        return job.getId();
    }

    @Transactional(readOnly = true)
    public JobResponse.GetJobCountByRegionDto getJobCountByRegion() {
        List<JobCountProjection> jobCounts = jobRepository.countJobsByRegion();

        int total = 0; // 전체 일자리 수를 저장하기 위한 변수

        JobResponse.GetJobCountByRegionDto.GetJobCountByRegionDtoBuilder dtoBuilder = JobResponse.GetJobCountByRegionDto.builder();

        for (JobCountProjection jobCount : jobCounts) {

            String region = jobCount.getRegion();
            Long count = jobCount.getCount();
            System.out.println(region+" " + count);
            switch (region) {
                case "SEOUL":
                    dtoBuilder.seoul(count.intValue());
                    break;
                case "INCHEON":
                    dtoBuilder.incheon(count.intValue());
                    break;
                case "BUSAN":
                    dtoBuilder.busan(count.intValue());
                    break;
                case "GYEONGGI":
                    dtoBuilder.gyeonggido(count.intValue());
                    break;
                case "CHUNGNAM":
                    dtoBuilder.chungcheongnamdo(count.intValue());
                    break;
                case "CHUNGBUK":
                    dtoBuilder.chungcheongbukdo(count.intValue());
                    break;
                case "JEONNAM":
                    dtoBuilder.jeollanamdo(count.intValue());
                    break;
                case "JEONBUK":
                    dtoBuilder.jeollabukdo(count.intValue());
                    break;
                case "GYEONGBUK":
                    dtoBuilder.gyeongsangbukdo(count.intValue());
                    break;
                case "GYEONGNAM":
                    dtoBuilder.gyeongsangnamdo(count.intValue());
                    break;
                case "GANGWON":
                    dtoBuilder.gangwondo(count.intValue());
                    break;
                case "JEJU":
                    dtoBuilder.jejudo(count.intValue());
                    break;
                default:
                    break;
            }

            // 전체 일자리 수에 더해줌
            total += count.intValue();
        }

        // 전체 일자리 수 설정
        dtoBuilder.all(total);

        // DTO 반환
        return dtoBuilder.build();

    }
}
