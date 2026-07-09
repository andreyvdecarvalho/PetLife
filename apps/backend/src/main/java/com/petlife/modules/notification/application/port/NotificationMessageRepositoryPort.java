package com.petlife.modules.notification.application.port;

import com.petlife.modules.notification.domain.entity.NotificationMessage;
import com.petlife.modules.notification.domain.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationMessageRepositoryPort {
    Page<NotificationMessage> findByUserId(UUID userId, Pageable pageable);
    Optional<NotificationMessage> findById(UUID id);
    NotificationMessage save(NotificationMessage message);
    void markAllAsRead(UUID userId);
    long countByUserIdAndCreatedAtAfter(UUID userId, LocalDateTime startOfDay);
    long countByUserIdAndCreatedAtAfterAndTypeNotIn(
            UUID userId,
            LocalDateTime startOfDay,
            List<NotificationType> types
    );
}
