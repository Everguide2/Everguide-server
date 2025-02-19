package com.example.everguide.service.education;

import com.example.everguide.api.code.status.ErrorStatus;
import com.example.everguide.api.exception.GeneralException;
import com.example.everguide.domain.Bookmark;
import com.example.everguide.domain.Education;
import com.example.everguide.domain.Job;
import com.example.everguide.domain.Member;
import com.example.everguide.domain.enums.BookmarkType;
import com.example.everguide.jwt.SecurityUtil;
import com.example.everguide.repository.BookmarkRepository;
import com.example.everguide.repository.EducationRepository;
import com.example.everguide.repository.MemberRepository;
import com.example.everguide.web.dto.education.EducationRequest;
import com.example.everguide.web.dto.education.EducationResponse;
import com.example.everguide.web.dto.job.JobRequest;
import com.example.everguide.web.dto.job.JobResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EducationService {
    private final EducationRepository educationRepository;
    private final EducationMappingService educationMappingService;
    private final SecurityUtil securityUtil;
    private final MemberRepository memberRepository;
    private final BookmarkRepository bookmarkRepository;



    @Transactional(readOnly = true)
    public Slice<Education> getWorthToGoList(Pageable pageable) {
        return educationRepository.findAllByOrderByEndDateAsc(pageable);
    }

    @Transactional(readOnly = true)
    public EducationResponse.NoLoginSearchEduByNameListDto noLoginSearchEduListByName(String keyword, Pageable pageable) {
        return educationMappingService.toNoLoginGetJobListSearchByName(educationRepository.searchEduListByName(keyword, pageable));
    }


    @Transactional
    public Bookmark addEduBookmark(EducationRequest.addEduBookmarkDto request) {
        // 회원 조회 -> 여기서 추후, 멤버 로그인 아이디로 조회해야 한다 -> 혜원님 푸시 이후 개발 하는 걸로
        String userId = securityUtil.getCurrentUserId();
        Member member = memberRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        // 교육 조회
        Education education = educationRepository.findById(request.getEducationId()).orElseThrow(() -> new GeneralException(ErrorStatus._EDUCATION_NOT_FOUND));
        if (bookmarkRepository.findByMemberAndEducation(member, education).isPresent()) {
            throw new GeneralException(ErrorStatus._EDUCATION_BOOKMARK_ALREADY_EXIST);
        }
        // 북마크 생성
        Bookmark bookmark = new Bookmark();
        bookmark.setType(BookmarkType.EDUCATION);
        bookmark.setMember(member);
        bookmark.setEducation(education);
        //북마크 저장
        return bookmarkRepository.save(bookmark);
    }

    @Transactional
    public Long deleteJobBookmark(EducationRequest.deleteEduBookmarkDto request) {

        String userId = securityUtil.getCurrentUserId();
        Member member = memberRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        // 일자리 조회
        Education education = educationRepository.findById(request.getEducationId()).orElseThrow(() -> new GeneralException(ErrorStatus._EDUCATION_NOT_FOUND));

        // 북마크가 DB에 존재하는 지 확인
        Bookmark bookmark = bookmarkRepository.findByMemberAndEducation(member, education).orElseThrow(() -> new GeneralException(ErrorStatus._EDUCATION_BOOKMARK_NOT_FOUND));

        bookmarkRepository.deleteById(bookmark.getId());
        return education.getId();
    }

    }
