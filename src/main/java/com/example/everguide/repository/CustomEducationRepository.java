package com.example.everguide.repository;

import com.example.everguide.domain.Education;
import com.example.everguide.domain.Job;
import com.example.everguide.domain.Member;
import com.example.everguide.web.dto.education.EducationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface CustomEducationRepository {
    Slice<Education> searchEduListByName(String name, Pageable pageable);

    Page<Education> noLoginGetEducationList(List<String> deadline, Pageable pageable, String keyWord);

    Page<Education> getEducationList(List<String> deadlines, Pageable pageable, String keyWord, Member member);

    EducationResponse.getEndDateCount countEducationByDeadline();
}
