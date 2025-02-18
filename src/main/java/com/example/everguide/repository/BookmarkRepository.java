package com.example.everguide.repository;

import com.example.everguide.domain.Bookmark;
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

    @Query("select b from Bookmark b where b.member.id = :memberId")
    List<Bookmark> findByMemberId(@Param("memberId") Long memberId);

    Optional<Bookmark> findByIdAndMemberId(Long bookmarkId, Long memberId);
}

