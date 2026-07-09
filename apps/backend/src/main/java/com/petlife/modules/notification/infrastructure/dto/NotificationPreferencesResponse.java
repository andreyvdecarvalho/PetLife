package com.petlife.modules.notification.infrastructure.dto;

import com.petlife.modules.notification.domain.entity.NotificationPreferences;
import java.time.LocalTime;
import java.util.UUID;

public record NotificationPreferencesResponse(
        UUID userId,
        boolean pushEnabled,
        boolean emailEnabled,
        boolean vaccines,
        boolean medications,
        boolean appointments,
        boolean grooming,
        boolean marketing,
        LocalTime doNotDisturbStart,
        LocalTime doNotDisturbEnd
) {
    public static NotificationPreferencesResponse fromEntity(NotificationPreferences entity) {
        return new NotificationPreferencesResponse(
                entity.getUserId(),
                entity.isPushEnabled(),
                entity.isEmailEnabled(),
                entity.isVaccines(),
                entity.isMedications(),
                entity.isAppointments(),
                entity.isGrooming(),
                entity.isMarketing(),
                entity.getDoNotDisturbStart(),
                entity.getDoNotDisturbEnd()
        );
    }
}
