package com.example.everguide.service.policy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.everguide.repository.PolicyRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PolicyQueryServiceImpl implements PolicyQueryService {
    
    private final PolicyRepository policyRepository;

    
} 