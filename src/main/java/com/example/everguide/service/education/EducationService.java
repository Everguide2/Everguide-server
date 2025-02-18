package com.example.everguide.service.education;

import com.example.everguide.domain.Education;
import com.example.everguide.repository.EducationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EducationService {
    private final EducationRepository educationRepository;

    public Slice<Education> getWorthToGoList(Pageable pageable) {
        return educationRepository.findAllByOrderByEndDateAsc(pageable);
    }
}
