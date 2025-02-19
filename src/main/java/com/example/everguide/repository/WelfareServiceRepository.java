package com.example.everguide.repository;

import com.example.everguide.domain.WelfareService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WelfareServiceRepository extends JpaRepository<WelfareService, String> {
    // 기본 CRUD 메서드와 추가 QueryDSL 기반 메서드를 함께 제공

    @Query("SELECT w FROM WelfareService w " +
           "WHERE (:ctpvNm IS NULL OR w.ctpvNm = :ctpvNm) " +
           "AND (:sggNm IS NULL OR w.sggNm = :sggNm) " +
           "AND (:intrsThemaNm IS NULL OR w.intrsThemaNmArray LIKE %:intrsThemaNm%) " +
           "AND (:lifeNm IS NULL OR w.lifeNmArray LIKE %:lifeNm%) " +
           "AND (:trgterIndvdlNm IS NULL OR w.trgterIndvdlNmArray LIKE %:trgterIndvdlNm%)")
    Page<WelfareService> findAllWithFilters(
            @Param("ctpvNm") String ctpvNm,
            @Param("sggNm") String sggNm,
            @Param("intrsThemaNm") String intrsThemaNm,
            @Param("lifeNm") String lifeNm,
            @Param("trgterIndvdlNm") String trgterIndvdlNm,
            Pageable pageable);

    @Query("SELECT DISTINCT w.lifeNmArray FROM WelfareService w WHERE w.lifeNmArray IS NOT NULL")
    List<String> findDistinctLifeNmArray();

    @Query("SELECT DISTINCT w.trgterIndvdlNmArray FROM WelfareService w WHERE w.trgterIndvdlNmArray IS NOT NULL")
    List<String> findDistinctTrgterIndvdlNmArray();

    @Query("SELECT DISTINCT w.intrsThemaNmArray FROM WelfareService w WHERE w.intrsThemaNmArray IS NOT NULL")
    List<String> findDistinctIntrsThemaNmArray();

    @Query("SELECT DISTINCT w.ctpvNm FROM WelfareService w WHERE w.ctpvNm IS NOT NULL")
    List<String> findDistinctCtpvNm();

    @Query("SELECT DISTINCT w.sggNm FROM WelfareService w WHERE w.sggNm IS NOT NULL")
    List<String> findDistinctSggNm();

    @Query("SELECT w FROM WelfareService w " +
           "WHERE (:keyword = '' OR w.servNm LIKE %:keyword% OR w.servDgst LIKE %:keyword%) " +
           "AND (:#{#intrsThemaNmArray.isEmpty()} = true OR w.intrsThemaNmArray IN :intrsThemaNmArray) " +
           "AND (:ctpvNm = '' OR w.ctpvNm = :ctpvNm) " +
           "AND (:lastModYmd = '' OR w.lastModYmd = :lastModYmd)")
    Page<WelfareService> searchWelfareServices(
        @Param("keyword") String keyword,
        @Param("intrsThemaNmArray") List<String> intrsThemaNmArray,
        @Param("ctpvNm") String ctpvNm,
        @Param("lastModYmd") String lastModYmd,
        Pageable pageable
    );
}
