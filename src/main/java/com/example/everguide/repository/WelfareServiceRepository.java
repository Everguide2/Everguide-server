package com.example.everguide.repository;

import com.example.everguide.domain.WelfareService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WelfareServiceRepository extends JpaRepository<WelfareService, String>, WelfareServiceRepositoryCustom {
    // 기본 CRUD 메서드와 추가 QueryDSL 기반 메서드를 함께 제공

    Optional<WelfareService> findById(Long id);

    @Query("SELECT w FROM WelfareService w " +
           "WHERE (:ctpvNm IS NULL OR w.region = :ctpvNm) " +
           "AND (:sggNm IS NULL OR w.regionDetail = :sggNm) " +
           "AND (:intrsThemaNm IS NULL OR w.supportTypes LIKE %:intrsThemaNm%) " +
           "AND (:lifeNm IS NULL OR w.lifeCycle LIKE %:lifeNm%) " +
           "AND (:trgterIndvdlNm IS NULL OR w.householdConditions LIKE %:trgterIndvdlNm%)")
    Page<WelfareService> findAllWithFilters(
            @Param("ctpvNm") String ctpvNm,
            @Param("sggNm") String sggNm,
            @Param("intrsThemaNm") String intrsThemaNm,
            @Param("lifeNm") String lifeNm,
            @Param("trgterIndvdlNm") String trgterIndvdlNm,
            Pageable pageable);

    @Query("SELECT DISTINCT w.lifeCycle FROM WelfareService w WHERE w.lifeCycle IS NOT NULL")
    List<String> findDistinctLifeNmArray();

    @Query("SELECT DISTINCT w.householdConditions FROM WelfareService w WHERE w.householdConditions IS NOT NULL")
    List<String> findDistinctTrgterIndvdlNmArray();

    @Query("SELECT DISTINCT w.supportTypes FROM WelfareService w WHERE w.supportTypes IS NOT NULL")
    List<String> findDistinctIntrsThemaNmArray();

    @Query("SELECT DISTINCT w.region FROM WelfareService w WHERE w.region IS NOT NULL")
    List<String> findDistinctCtpvNm();

    @Query("SELECT DISTINCT w.regionDetail FROM WelfareService w WHERE w.regionDetail IS NOT NULL")
    List<String> findDistinctSggNm();

    @Query("SELECT w FROM WelfareService w " +
           "WHERE (:keyword = '' OR w.serviceName LIKE %:keyword% OR w.serviceDigest LIKE %:keyword%) " +
           "AND (:#{#intrsThemaNmArray.isEmpty()} = true OR w.supportTypes IN :intrsThemaNmArray) " +
           "AND (:ctpvNm = '' OR w.region = :ctpvNm) " +
           "AND (:lastModYmd = '' OR w.lastModYmd = :lastModYmd)")
    Page<WelfareService> searchWelfareServices(
        @Param("keyword") String keyword,
        @Param("intrsThemaNmArray") List<String> intrsThemaNmArray,
        @Param("ctpvNm") String ctpvNm,
        @Param("lastModYmd") String lastModYmd,
        Pageable pageable
    );
}
