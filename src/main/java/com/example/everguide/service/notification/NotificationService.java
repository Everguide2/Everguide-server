package com.example.everguide.service.notification;

import com.example.everguide.domain.enums.NotifyType;
import com.example.everguide.web.dto.notification.NotificationResponse;

import java.util.List;

public interface NotificationService {
    
    // 알림 생성
    void createNotification(Long memberId, String title, String content, NotifyType type, String resourceUrl);
    
    // 알림 목록 조회
    List<NotificationResponse> getNotifications(Long memberId);
    
    // 읽지 않은 알림 개수 조회
    long getUnreadNotificationCount(Long memberId);
    
    // 알림 읽음 처리
    void markAsRead(Long notificationId);
    
    // 만료된 알림 삭제
    void deleteExpiredNotifications();
    
    // 특정 타입의 알림 목록 조회
    List<NotificationResponse> getNotificationsByType(Long memberId, NotifyType type);
} 