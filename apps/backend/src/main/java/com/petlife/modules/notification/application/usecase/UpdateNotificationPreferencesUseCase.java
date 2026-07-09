package com.petlife.modules.notification.application.usecase;

import com.petlife.modules.notification.application.port.NotificationPreferencesRepositoryPort;
import com.petlife.modules.notification.domain.entity.NotificationPreferences;
import com.petlife.modules.notification.infrastructure.dto.NotificationPreferencesRequest;
import com.petlife.modules.notification.infrastructure.dto.NotificationPreferencesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UpdateNotificationPreferencesUseCase {

    private final NotificationPreferencesRepositoryPort preferencesRepository;

    @Transactional
    public NotificationPreferencesResponse execute(UUID userId, NotificationPreferencesRequest request) {
        NotificationPreferences prefs = preferencesRepository.findByUserId(userId)
                .orElseGet(() -> {
                    NotificationPreferences newPrefs = new NotificationPreferences();
                    newPrefs.setUserId(userId);
                    return newPrefs;
                });

        prefs.setPushEnabled(request.pushEnabled());
        prefs.setEmailEnabled(request.emailEnabled());
        prefs.setVaccines(request.vaccines());
        prefs.setMedications(request.medications());
        prefs.setAppointments(request.appointments());
        prefs.setGrooming(request.grooming());
        prefs.setMarketing(request.marketing());
        prefs.setDoNotDisturbStart(request.doNotDisturbStart());
        prefs.setDoNotDisturbEnd(request.doNotDisturbEnd());

        NotificationPreferences saved = preferencesRepository.save(prefs);
        return NotificationPreferencesResponse.fromEntity(saved);
    }
}
