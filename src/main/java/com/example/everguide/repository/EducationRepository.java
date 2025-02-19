package com.example.everguide.repository;

import com.example.everguide.domain.Education;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> , CustomEducationRepository {
    Slice<Education> findAllByOrderByEndDateAsc(Pageable pageable);

    @Query(value = "SELECT * FROM Education ORDER BY RAND() LIMIT 6", nativeQuery = true)
    List<Education> getRandom6Educations();

    @Query("SELECT CONCAT(e.educationKey1, '-', e.educationKey2) " +
            "FROM Education e " +
            "WHERE e.educationKey1 IN :key1List AND e.educationKey2 IN :key2List")
    Set<String> findDuplicateEducationKeyList(@Param("key1List") List<String> educationKey1List,
                                              @Param("key2List") List<String> educationKey2List);
}