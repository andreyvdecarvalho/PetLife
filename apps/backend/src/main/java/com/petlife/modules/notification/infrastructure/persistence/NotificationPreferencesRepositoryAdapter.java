package com.petlife.modules.notification.infrastructure.persistence;

import com.petlife.modules.notification.application.port.NotificationPreferencesRepositoryPort;
import com.petlife.modules.notification.domain.entity.NotificationPreferences;
import com.petlife.modules.notification.infrastructure.persistence.mapper.NotificationPreferencesMapper;
import com.petlife.modules.notification.infrastructure.persistence.entity.NotificationPreferencesJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationPreferencesRepositoryAdapter implements NotificationPreferencesRepositoryPort {

    private final NotificationPreferencesJpaRepository jpaRepository;
    private final NotificationPreferencesMapper mapper;

    @Override
    public Optional<NotificationPreferences> findByUserId(UUID userId) {
        return jpaRepository.findById(userId).map(mapper::toDomain);
    }

    @Override
    public NotificationPreferences save(NotificationPreferences preferences) {
        NotificationPreferencesJpaEntity entity = mapper.toEntity(preferences);
        NotificationPreferencesJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
