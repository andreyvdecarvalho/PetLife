package com.petlife.modules.notification.infrastructure.persistence;

import com.petlife.modules.notification.application.port.NotificationMessageRepositoryPort;
import com.petlife.modules.notification.domain.entity.NotificationMessage;
import com.petlife.modules.notification.domain.entity.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationMessageRepositoryAdapter implements NotificationMessageRepositoryPort {

    private final NotificationMessageJpaRepository jpaRepository;

    @Override
    public Page<NotificationMessage> findByUserId(UUID userId, Pageable pageable) {
        return jpaRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Override
    public Optional<NotificationMessage> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public NotificationMessage save(NotificationMessage message) {
        return jpaRepository.save(message);
    }

    @Override
    public void markAllAsRead(UUID userId) {
        jpaRepository.markAllAsRead(userId);
    }

    @Override
    public long countByUserIdAndCreatedAtAfter(UUID userId, LocalDateTime startOfDay) {
        return jpaRepository.countByUserIdAndCreatedAtAfterAndTypeNotIn(
                userId, startOfDay, java.util.Collections.emptyList()
        );
    }

    @Override
    public long countByUserIdAndCreatedAtAfterAndTypeNotIn(
            UUID userId,
            LocalDateTime startOfDay,
            List<NotificationType> types
    ) {
        return jpaRepository.countByUserIdAndCreatedAtAfterAndTypeNotIn(userId, startOfDay, types);
    }
}
