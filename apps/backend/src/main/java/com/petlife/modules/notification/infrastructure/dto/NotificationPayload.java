package com.petlife.modules.notification.infrastructure.dto;

import com.petlife.modules.notification.domain.entity.NotificationType;
import java.io.Serializable;
import java.util.UUID;

public record NotificationPayload(
        UUID userId,
        NotificationType type,
        String title,
        String body,
        UUID targetId
) implements Serializable {}
