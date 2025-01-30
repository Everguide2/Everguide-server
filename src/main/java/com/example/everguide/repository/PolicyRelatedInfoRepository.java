package com.example.everguide.repository;

import com.example.everguide.domain.PolicyRelatedInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyRelatedInfoRepository extends JpaRepository<PolicyRelatedInfo, Long> {
} 