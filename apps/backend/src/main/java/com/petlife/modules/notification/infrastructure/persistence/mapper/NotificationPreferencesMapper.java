package com.petlife.modules.notification.infrastructure.persistence.mapper;

import com.petlife.modules.notification.domain.entity.NotificationPreferences;
import com.petlife.modules.notification.infrastructure.persistence.entity.NotificationPreferencesJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationPreferencesMapper {

    public NotificationPreferences toDomain(NotificationPreferencesJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        NotificationPreferences domain = new NotificationPreferences();
        domain.setUserId(entity.getUserId());
        domain.setPushEnabled(entity.isPushEnabled());
        domain.setEmailEnabled(entity.isEmailEnabled());
        domain.setVaccines(entity.isVaccines());
        domain.setMedications(entity.isMedications());
        domain.setAppointments(entity.isAppointments());
        domain.setGrooming(entity.isGrooming());
        domain.setMarketing(entity.isMarketing());
        domain.setDoNotDisturbStart(entity.getDoNotDisturbStart());
        domain.setDoNotDisturbEnd(entity.getDoNotDisturbEnd());
        return domain;
    }

    public NotificationPreferencesJpaEntity toEntity(NotificationPreferences domain) {
        if (domain == null) {
            return null;
        }
        NotificationPreferencesJpaEntity entity = new NotificationPreferencesJpaEntity();
        entity.setUserId(domain.getUserId());
        entity.setPushEnabled(domain.isPushEnabled());
        entity.setEmailEnabled(domain.isEmailEnabled());
        entity.setVaccines(domain.isVaccines());
        entity.setMedications(domain.isMedications());
        entity.setAppointments(domain.isAppointments());
        entity.setGrooming(domain.isGrooming());
        entity.setMarketing(domain.isMarketing());
        entity.setDoNotDisturbStart(domain.getDoNotDisturbStart());
        entity.setDoNotDisturbEnd(domain.getDoNotDisturbEnd());
        return entity;
    }
}
