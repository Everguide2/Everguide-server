package com.example.everguide.repository;

import com.example.everguide.domain.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.everguide.domain.Job;
import com.example.everguide.domain.Member;
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

    @Query("SELECT b FROM Bookmark b WHERE b.member.id = :memberId")
    Page<Bookmark> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    Optional<Bookmark> findByIdAndMemberId(Long bookmarkId, Long memberId);

    Bookmark findOneByMember(Member member);

    Optional<Bookmark> findByMemberAndJob(Member member, Job job);

    boolean existsByJobAndMember(Job job, Member member);
}
