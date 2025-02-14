package com.example.everguide.repository;

import com.example.everguide.domain.Member;
import com.example.everguide.domain.enums.ProviderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUserId(String userId);

    Optional<Member> findByEmail(String email);

    Boolean existsByUserId(String userId);

    void deleteByUserId(String userId);
}