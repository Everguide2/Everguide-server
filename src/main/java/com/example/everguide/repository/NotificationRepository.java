package com.example.everguide.repository;

import com.example.everguide.domain.Notification;
import com.example.everguide.domain.enums.NotifyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByMemberIdOrderByCreatedAtDesc(Long memberId);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.member.id = :memberId AND n.isRead = false")
    long countUnreadNotifications(Long memberId);
    
    List<Notification> findByMemberIdAndTypeOrderByCreatedAtDesc(Long memberId, NotifyType type);
    
    @Query("SELECT n FROM Notification n WHERE n.expirationDate < :now")
    List<Notification> findExpiredNotifications(LocalDateTime now);
} 