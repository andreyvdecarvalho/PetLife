package com.petlife.modules.notification.domain.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class NotificationMessage {

    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UUID userId;
    private NotificationType type;
    private String title;
    private String body;
    private UUID targetId;
    private boolean read = false;
}
