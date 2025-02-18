package com.example.everguide.repository;

import com.example.everguide.domain.Education;
import com.example.everguide.domain.Job;
import com.example.everguide.domain.Member;
import com.example.everguide.domain.Notification;
import com.example.everguide.domain.enums.NotifyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 사용자의 알림 목록: 생성일(desc) 순
    List<Notification> findByMemberOrderByCreatedDateDesc(Member member);

    // 특정 사용자의 알림 개수
    Long countByMember(Member member);

    // 특정 사용자의 읽지 않은 알림만 조회
    List<Notification> findByMemberAndIsReadFalseOrderByCreatedDateDesc(Member member);

    // 회원과 특정 타입(WELCOME)의 알림 조회
    List<Notification> findByMemberAndType(Member member, NotifyType type);




    // 30개 초과 시 오래된 알림 삭제 (JPA 메서드 예시)
    @Modifying
    @Query("""
            DELETE FROM Notification n 
            WHERE n.id IN (
                SELECT n2.id FROM Notification n2 
                WHERE n2.member = :member 
                ORDER BY n2.createdDate ASC
            )
            AND n.id NOT IN (
                SELECT n3.id FROM Notification n3 
                WHERE n3.member = :member 
                ORDER BY n3.createdDate DESC
                LIMIT 30
            )
            """)
    void deleteOldNotifications(Member member);
}
