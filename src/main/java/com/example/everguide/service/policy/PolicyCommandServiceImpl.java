package com.example.everguide.service.policy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.everguide.repository.PolicyRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class PolicyCommandServiceImpl implements PolicyCommandService {
    
    private final PolicyRepository policyRepository;
} 