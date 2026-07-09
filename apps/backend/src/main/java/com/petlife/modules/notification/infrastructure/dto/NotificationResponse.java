package com.petlife.modules.notification.infrastructure.dto;

import com.petlife.modules.notification.domain.entity.NotificationMessage;
import com.petlife.modules.notification.domain.entity.NotificationType;
import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID userId,
        NotificationType type,
        String title,
        String body,
        UUID targetId,
        boolean read,
        LocalDateTime createdAt
) {
    public static NotificationResponse fromEntity(NotificationMessage entity) {
        return new NotificationResponse(
                entity.getId(),
                entity.getUserId(),
                entity.getType(),
                entity.getTitle(),
                entity.getBody(),
                entity.getTargetId(),
                entity.isRead(),
                entity.getCreatedAt()
        );
    }
}
