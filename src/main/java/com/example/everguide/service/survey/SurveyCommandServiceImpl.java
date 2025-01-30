package com.example.everguide.service.survey;

import com.example.everguide.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SurveyCommandServiceImpl implements SurveyCommandService {
    private final SurveyRepository surveyRepository;
} 