package com.example.everguide.web.dto.notificaton;

import com.example.everguide.domain.Notification;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationDto {
    private Long id;
    private String title;
    private String message;
    private String color;
    private boolean isRead;

    public static NotificationDto fromEntity(Notification entity) {
        NotificationDto dto = new NotificationDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setMessage(entity.getMessage());
        dto.setColor(entity.getColor());
        dto.setRead(entity.isRead());
        return dto;
    }
}
