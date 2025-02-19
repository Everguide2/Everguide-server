package com.example.everguide.repository;

import com.example.everguide.domain.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {

    @Query("select s from Survey s where s.member.userId = :userId")
    Survey findByUserId(@Param("userId") String userId);
} 