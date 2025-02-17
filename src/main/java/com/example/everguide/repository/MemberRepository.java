package com.example.everguide.repository;

import com.example.everguide.domain.Member;
import com.example.everguide.domain.enums.ProviderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByPhoneNumber(String phoneNumber);

    Boolean existsByPhoneNumber(String phoneNumber);

    Optional<Member> findByUserId(String userId);

    List<Member> findByNameAndPhoneNumberAndProviderType(String name, String phoneNumber, ProviderType providerType);

    Optional<Member> findByEmailAndNameAndPhoneNumberAndProviderType(String email, String name, String phoneNumber, ProviderType providerType);

    Boolean existsByUserId(String userId);

    void deleteByUserId(String userId);

    @Modifying
    @Query("UPDATE Member m SET m.password = :newPassword WHERE m.userId = :userId")
    int updatePasswordByUserId(@Param("userId") String userId, @Param("newPassword") String newPassword);
}