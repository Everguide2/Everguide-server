package com.example.everguide.web.dto.notification;

import com.example.everguide.domain.Notification;
import com.example.everguide.domain.enums.NotifyType;

import java.time.LocalDateTime;

public record NotificationResponse(
    Long id,
    String title,
    String content,
    NotifyType type,
    String resourceUrl,
    boolean isRead,
    LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
            notification.getId(),
            notification.getTitle(),
            notification.getContent(),
            notification.getType(),
            notification.getResourceUrl(),
            notification.getIsRead(),
            notification.getCreatedAt()
        );
    }
} 