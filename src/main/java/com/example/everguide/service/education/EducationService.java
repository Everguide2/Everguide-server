package com.example.everguide.service.education;

import com.example.everguide.domain.Education;
import com.example.everguide.repository.EducationRepository;
import com.example.everguide.web.dto.education.EducationResponse;
import com.example.everguide.web.dto.job.JobResponse;
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
    @Transactional(readOnly = true)
    public Slice<Education> getWorthToGoList(Pageable pageable) {
        return educationRepository.findAllByOrderByEndDateAsc(pageable);
    }

    @Transactional(readOnly = true)
    public EducationResponse.NoLoginSearchEduByNameListDto noLoginSearchEduListByName(String keyword, Pageable pageable) {
        return educationMappingService.toNoLoginGetJobListSearchByName(educationRepository.searchEduListByName(keyword, pageable));
    }


    }
