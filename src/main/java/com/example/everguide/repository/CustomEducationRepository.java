package com.example.everguide.repository;

import com.example.everguide.domain.Education;
import com.example.everguide.domain.Job;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomEducationRepository {
    Slice<Education> searchEduListByName(String name, Pageable pageable);

}
