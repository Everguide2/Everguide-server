package com.example.everguide.service.education;

import com.example.everguide.repository.EducationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EducationQueryServiceImpl implements EducationQueryService {

    private final EducationRepository educationRepository;
}
