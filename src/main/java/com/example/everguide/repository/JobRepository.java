package com.example.everguide.repository;

import com.example.everguide.domain.Job;
import com.example.everguide.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, CustomJobRepository {
    @Query("SELECT j.jobCode FROM Job j WHERE j.jobCode IN :newJobCode")
    Set<String> findDuplicateJobCodeList(@Param("newJobCode") List<String> newJobCode);

    Integer countByNameContainingIgnoreCase(String name);

    // Region 별로 그룹화하여 카운트
    @Query("SELECT j.region AS region, COUNT(j) AS count FROM Job j GROUP BY j.region")
    List<JobCountProjection> countJobsByRegion();
}