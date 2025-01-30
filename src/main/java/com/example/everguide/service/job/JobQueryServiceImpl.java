package com.example.everguide.service.job;

import com.example.everguide.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobQueryServiceImpl implements JobQueryService {

    private final JobRepository jobRepository;
} 