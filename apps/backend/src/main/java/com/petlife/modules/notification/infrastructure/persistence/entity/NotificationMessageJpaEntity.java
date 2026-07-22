package com.petlife.modules.notification.infrastructure.persistence.entity;

import com.petlife.modules.notification.domain.entity.NotificationType;
import com.petlife.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "notification_messages")
@Getter
@Setter
public class NotificationMessageJpaEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "body", nullable = false, length = 1000)
    private String body;

    @Column(name = "target_id")
    private UUID targetId;

    @Column(name = "read", nullable = false)
    private boolean read = false;
}
