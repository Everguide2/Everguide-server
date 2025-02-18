package com.example.everguide.repository;

import com.example.everguide.domain.Education;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
    Slice<Education> findAllByOrderByEndDateAsc(Pageable pageable);
}