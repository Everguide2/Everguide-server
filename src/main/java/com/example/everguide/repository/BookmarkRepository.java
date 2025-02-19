package com.example.everguide.repository;

import com.example.everguide.domain.Bookmark;
import com.example.everguide.domain.Job;
import com.example.everguide.domain.Member;
import com.example.everguide.domain.WelfareService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    @Query("select b from Bookmark b where b.member.userId = :userId")
    List<Bookmark> findByUserId(@Param("userId") String userId);

    Bookmark findOneByMember(Member member);

    Optional<Bookmark> findByMemberAndJob(Member member, Job job);

    Boolean existsByJobAndMember(Job job, Member member);

    // 복지 서비스 북마크 관련 메서드
    @Query("SELECT b FROM Bookmark b WHERE b.member = :member AND b.welfareService = :welfareService")
    Optional<Bookmark> findByMemberAndWelfareService(@Param("member") Member member, @Param("welfareService") WelfareService welfareService);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Bookmark b WHERE b.member = :member AND b.welfareService = :welfareService")
    boolean existsByMemberAndWelfareService(@Param("member") Member member, @Param("welfareService") WelfareService welfareService);

    @Query("SELECT COUNT(b) > 0 FROM Bookmark b WHERE b.member.id = :memberId AND b.welfareService.servId = :welfareServiceId")
    boolean existsByMemberIdAndWelfareServiceId(@Param("memberId") Long memberId, @Param("welfareServiceId") String welfareServiceId);

    // 회원의 복지 서비스 북마크 목록 조회
    @Query("SELECT b FROM Bookmark b WHERE b.member = :member AND b.type = 'WELFARE'")
    List<Bookmark> findWelfareBookmarksByMember(@Param("member") Member member);
}