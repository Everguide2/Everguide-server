package com.example.everguide.service.education;

import com.example.everguide.domain.Education;
import com.example.everguide.repository.EducationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EducationService {
    private final EducationRepository educationRepository;

    public List<Education> getWorthToGoList(Pageable pageable) {
        return educationRepository.findAllByOrderByEndDateAsc(pageable).getContent();
    }
}
