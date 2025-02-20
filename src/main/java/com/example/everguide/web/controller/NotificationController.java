package com.example.everguide.web.controller;

import com.example.everguide.domain.Member;
import com.example.everguide.domain.Notification;
import com.example.everguide.jwt.SecurityUtil;
import com.example.everguide.web.notification.NotificationService;
import com.example.everguide.web.dto.notificaton.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 알림(Notification) 관련 API
 * - 로그인된 사용자 정보를 SecurityUtil에서 가져와 사용
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SecurityUtil securityUtil;

    /**
     * 로그인된 회원의 전체 알림 목록 조회
     * 예: GET /notifications
     */
    @GetMapping
    public List<NotificationDto> getNotifications() {
        String userId = securityUtil.getCurrentUserId();
        List<Notification> notifications = notificationService.getNotificationsForUserId(userId);
        return notifications.stream()
                .map(NotificationDto::fromEntity)
                .toList();
    }

    /**
     * 로그인된 회원의 '읽지 않은' 알림 목록 조회
     * 예: GET /notifications/unread
     */
    @GetMapping("/unread")
    public List<NotificationDto> getUnreadNotifications() {
        String userId = securityUtil.getCurrentUserId();
        List<Notification> unreadNotifications = notificationService.getUnreadNotificationsForUserId(userId);
        return unreadNotifications.stream()
                .map(NotificationDto::fromEntity)
                .toList();
    }

    /**
     * 알림 읽음 처리
     * 예: POST /notifications/{notificationId}/read
     */
    @PostMapping("/{notificationId}/read")
    public void markAsRead(@PathVariable Long notificationId) {
        String userId = securityUtil.getCurrentUserId();
        notificationService.markAsReadForUser(notificationId, userId);
    }
}
