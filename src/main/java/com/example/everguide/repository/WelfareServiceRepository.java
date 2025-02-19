package com.example.everguide.repository;

import com.example.everguide.domain.WelfareService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WelfareServiceRepository extends JpaRepository<WelfareService, String>, WelfareServiceRepositoryCustom {
    // 기본 CRUD 메서드와 추가 QueryDSL 기반 메서드를 함께 제공

    Optional<WelfareService> findById(Long id);
}
