package com.example.everguide.service.job;

import com.example.everguide.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class JobCommandServiceImpl implements JobCommandService {
    private final JobRepository jobRepository;
} 