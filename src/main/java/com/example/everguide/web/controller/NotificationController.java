package com.example.everguide.web.controller;

import com.example.everguide.api.ApiResponse;
import com.example.everguide.api.code.status.SuccessStatus;
import com.example.everguide.domain.enums.NotifyType;
import com.example.everguide.service.notification.NotificationService;
import com.example.everguide.web.dto.notification.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<List<NotificationResponse>> getNotifications(@RequestParam Long memberId) {
        List<NotificationResponse> notifications = notificationService.getNotifications(memberId);
        return ApiResponse.onSuccess(SuccessStatus._OK, notifications);
    }

    @GetMapping("/unread/count")
    public ApiResponse<Long> getUnreadCount(@RequestParam Long memberId) {
        long count = notificationService.getUnreadNotificationCount(memberId);
        return ApiResponse.onSuccess(SuccessStatus._OK, count);
    }

    @GetMapping("/type/{type}")
    public ApiResponse<List<NotificationResponse>> getNotificationsByType(
            @RequestParam Long memberId,
            @PathVariable NotifyType type) {
        List<NotificationResponse> notifications = notificationService.getNotificationsByType(memberId, type);
        return ApiResponse.onSuccess(SuccessStatus._OK, notifications);
    }

    @PatchMapping("/{notificationId}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }
} 