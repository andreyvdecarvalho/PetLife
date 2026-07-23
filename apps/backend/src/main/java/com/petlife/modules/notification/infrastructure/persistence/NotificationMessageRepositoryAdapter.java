package com.petlife.modules.notification.infrastructure.persistence;

import com.petlife.modules.notification.application.port.NotificationMessageRepositoryPort;
import com.petlife.modules.notification.application.usecase.PagedResult;
import com.petlife.modules.notification.domain.entity.NotificationMessage;
import com.petlife.modules.notification.domain.entity.NotificationType;
import com.petlife.modules.notification.infrastructure.persistence.entity.NotificationMessageJpaEntity;
import com.petlife.shared.response.PageMeta;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificationMessageRepositoryAdapter implements NotificationMessageRepositoryPort {

    private final NotificationMessageJpaRepository jpaRepository;

    @Override
    public PagedResult<NotificationMessage> findByUserId(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<NotificationMessageJpaEntity> jpaPage =
                jpaRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        List<NotificationMessage> content = jpaPage.getContent().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());

        PageMeta meta = new PageMeta(
                jpaPage.getNumber(),
                jpaPage.getSize(),
                jpaPage.getTotalElements(),
                jpaPage.getTotalPages()
        );

        return new PagedResult<>(content, meta);
    }

    @Override
    public Optional<NotificationMessage> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public NotificationMessage save(NotificationMessage message) {
        NotificationMessageJpaEntity entity = toEntity(message);
        NotificationMessageJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    @Transactional
    public void markAllAsRead(UUID userId) {
        jpaRepository.markAllAsRead(userId);
    }

    @Override
    public long countByUserIdAndCreatedAtAfter(UUID userId, LocalDateTime startOfDay) {
        return jpaRepository.countByUserIdAndCreatedAtAfterAndTypeNotIn(
                userId, startOfDay, Collections.emptyList()
        );
    }

    @Override
    public long countByUserIdAndCreatedAtAfterAndTypeNotIn(
            UUID userId,
            LocalDateTime startOfDay,
            List<NotificationType> types) {
        return jpaRepository.countByUserIdAndCreatedAtAfterAndTypeNotIn(userId, startOfDay, types);
    }

    // ─── Mapeamento inline (sem classe de mapper separada) ───────────────────

    private NotificationMessage toDomain(NotificationMessageJpaEntity e) {
        NotificationMessage domain = new NotificationMessage();
        domain.setId(e.getId());
        domain.setCreatedAt(e.getCreatedAt());
        domain.setUpdatedAt(e.getUpdatedAt());
        domain.setUserId(e.getUserId());
        domain.setType(e.getType());
        domain.setTitle(e.getTitle());
        domain.setBody(e.getBody());
        domain.setTargetId(e.getTargetId());
        domain.setRead(e.isRead());
        return domain;
    }

    private NotificationMessageJpaEntity toEntity(NotificationMessage domain) {
        NotificationMessageJpaEntity entity = new NotificationMessageJpaEntity();
        entity.setId(domain.getId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUserId(domain.getUserId());
        entity.setType(domain.getType());
        entity.setTitle(domain.getTitle());
        entity.setBody(domain.getBody());
        entity.setTargetId(domain.getTargetId());
        entity.setRead(domain.isRead());
        return entity;
    }
}
