package com.petlife.modules.notification.infrastructure.persistence;

import com.petlife.modules.notification.application.port.NotificationPreferencesRepositoryPort;
import com.petlife.modules.notification.domain.entity.NotificationPreferences;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationPreferencesRepositoryAdapter implements NotificationPreferencesRepositoryPort {

    private final NotificationPreferencesJpaRepository jpaRepository;

    @Override
    public Optional<NotificationPreferences> findByUserId(UUID userId) {
        return jpaRepository.findById(userId);
    }

    @Override
    public NotificationPreferences save(NotificationPreferences preferences) {
        return jpaRepository.save(preferences);
    }
}
