package com.petlife.modules.notification.infrastructure.persistence;

import com.petlife.modules.notification.domain.entity.NotificationType;
import com.petlife.modules.notification.infrastructure.persistence.entity.NotificationMessageJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Repository
public interface NotificationMessageJpaRepository
        extends JpaRepository<NotificationMessageJpaEntity, UUID> {

    Page<NotificationMessageJpaEntity> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    @Modifying
    @Query("UPDATE NotificationMessageJpaEntity n SET n.read = true WHERE n.userId = :userId AND n.read = false")
    void markAllAsRead(UUID userId);

    long countByUserIdAndCreatedAtAfterAndTypeNotIn(
            UUID userId,
            LocalDateTime startOfDay,
            Collection<NotificationType> types
    );
}
