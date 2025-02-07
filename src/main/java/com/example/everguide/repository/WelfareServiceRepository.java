package com.example.everguide.repository;

import com.example.everguide.domain.WelfareService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WelfareServiceRepository extends JpaRepository<WelfareService, String> {
    // 기본 CRUD 메서드와 추가 QueryDSL 기반 메서드를 함께 제공
}
