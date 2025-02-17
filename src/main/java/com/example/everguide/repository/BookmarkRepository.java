package com.example.everguide.repository;

import com.example.everguide.domain.Bookmark;
import com.example.everguide.domain.Job;
import com.example.everguide.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Bookmark findOneByMember(Member member);

    Optional<Bookmark> findByMemberAndJob(Member member, Job job);
}