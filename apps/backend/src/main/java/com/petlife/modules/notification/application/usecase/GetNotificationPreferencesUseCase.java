package com.petlife.modules.notification.application.usecase;

import com.petlife.modules.notification.application.port.NotificationPreferencesRepositoryPort;
import com.petlife.modules.notification.domain.entity.NotificationPreferences;
import com.petlife.modules.notification.infrastructure.dto.NotificationPreferencesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetNotificationPreferencesUseCase {

    private final NotificationPreferencesRepositoryPort preferencesRepository;

    @Transactional
    public NotificationPreferencesResponse execute(UUID userId) {
        NotificationPreferences prefs = preferencesRepository.findByUserId(userId)
                .orElseGet(() -> {
                    NotificationPreferences newPrefs = new NotificationPreferences();
                    newPrefs.setUserId(userId);
                    return preferencesRepository.save(newPrefs);
                });
        return NotificationPreferencesResponse.fromEntity(prefs);
    }
}
