package com.example.everguide.service.survey;

import com.example.everguide.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyQueryServiceImpl implements SurveyQueryService {

    private final SurveyRepository surveyRepository;
} 