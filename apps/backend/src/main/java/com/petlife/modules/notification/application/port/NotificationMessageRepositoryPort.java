package com.petlife.modules.notification.application.port;

import com.petlife.modules.notification.application.usecase.PagedResult;
import com.petlife.modules.notification.domain.entity.NotificationMessage;
import com.petlife.modules.notification.domain.entity.NotificationType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationMessageRepositoryPort {
    /**
     * Retorna página de mensagens de um usuário.
     * Parâmetros de paginação são inteiros simples — sem acoplamento ao Spring Data.
     */
    PagedResult<NotificationMessage> findByUserId(UUID userId, int page, int size);

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
