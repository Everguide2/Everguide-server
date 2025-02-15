package com.example.everguide.service.job;

import com.example.everguide.domain.Job;
import com.example.everguide.domain.enums.Region;
import com.example.everguide.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepository;

    public List<Job> getJobList(List<Region> regionList, String sortBy, Boolean isRecruiting, Pageable pageable) {
        return jobRepository.findJobList(regionList, sortBy, isRecruiting, pageable);
    }

}
