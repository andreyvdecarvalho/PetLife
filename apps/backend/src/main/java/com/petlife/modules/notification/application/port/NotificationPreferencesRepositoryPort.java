package com.petlife.modules.notification.application.port;

import com.petlife.modules.notification.domain.entity.NotificationPreferences;
import java.util.Optional;
import java.util.UUID;

public interface NotificationPreferencesRepositoryPort {
    Optional<NotificationPreferences> findByUserId(UUID userId);
    NotificationPreferences save(NotificationPreferences preferences);
}
