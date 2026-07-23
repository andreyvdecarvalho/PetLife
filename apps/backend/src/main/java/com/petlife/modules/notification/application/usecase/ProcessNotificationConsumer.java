package com.petlife.modules.notification.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.notification.application.port.FcmServicePort;
import com.petlife.modules.notification.application.port.NotificationMessageRepositoryPort;
import com.petlife.modules.notification.application.port.NotificationPreferencesRepositoryPort;
import com.petlife.modules.notification.domain.entity.NotificationMessage;
import com.petlife.modules.notification.domain.entity.NotificationPreferences;
import com.petlife.modules.notification.domain.entity.NotificationType;
import com.petlife.modules.notification.infrastructure.dto.NotificationPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessNotificationConsumer {

    private final UserRepositoryPort userRepository;
    private final NotificationPreferencesRepositoryPort preferencesRepository;
    private final NotificationMessageRepositoryPort messageRepository;
    private final FcmServicePort fcmService;

    @Transactional
    public void execute(NotificationPayload payload) {
        log.info("Processing notification payload: {}", payload);

        User user = userRepository.findById(payload.userId()).orElse(null);
        if (user == null) {
            log.warn("User {} not found, discarding notification", payload.userId());
            return;
        }

        NotificationPreferences preferences = preferencesRepository.findByUserId(payload.userId())
                .orElseGet(() -> {
                    NotificationPreferences newPrefs = new NotificationPreferences();
                    newPrefs.setUserId(payload.userId());
                    return newPrefs;
                });

        if (!preferences.isPushEnabled() && !preferences.isEmailEnabled()) {
            log.info("Notification disabled globally for user {}, discarding", payload.userId());
            return;
        }

        if (!isCategoryEnabled(payload.type(), preferences)) {
            log.info("Notification category {} disabled for user {}, discarding", payload.type(), payload.userId());
            return;
        }

        ZoneId zoneId = ZoneId.of(user.getTimezone() != null ? user.getTimezone().getZoneId() : "America/Sao_Paulo");
        ZonedDateTime nowInUserTz = ZonedDateTime.now(zoneId);
        LocalTime nowTime = nowInUserTz.toLocalTime();
        if (isTimeInDnd(nowTime, preferences.getDoNotDisturbStart(), preferences.getDoNotDisturbEnd())) {
            log.info("User {} is in DND window, discarding notification", payload.userId());
            return;
        }

        if (!isMedicationType(payload.type())) {
            LocalDateTime startOfDay = nowInUserTz.toLocalDate().atStartOfDay();
            long count = messageRepository.countByUserIdAndCreatedAtAfterAndTypeNotIn(
                    payload.userId(),
                    startOfDay,
                    List.of(NotificationType.MEDICATION_REMINDER, 
                            NotificationType.MEDICATION_DOSE, 
                            NotificationType.MEDICATION_LATE)
            );
            if (count >= 5) {
                log.info("User {} has reached daily limit of 5 non-medication notifications, discarding",
                        payload.userId());
                return;
            }
        }

        NotificationMessage message = new NotificationMessage();
        message.setUserId(payload.userId());
        message.setType(payload.type());
        message.setTitle(payload.title());
        message.setBody(payload.body());
        message.setTargetId(payload.targetId());
        message.setRead(false);
        messageRepository.save(message);

        if (preferences.isPushEnabled() && user.getFcmToken() != null && !user.getFcmToken().isBlank()) {
            fcmService.sendPushNotification(user.getFcmToken(), payload.title(), payload.body());
        }
    }

    private boolean isCategoryEnabled(NotificationType type, NotificationPreferences prefs) {
        return switch (type) {
            case VACCINE_REMINDER, VACCINATION_DUE -> prefs.isVaccines();
            case MEDICATION_REMINDER, MEDICATION_DOSE, MEDICATION_LATE -> prefs.isMedications();
            case CONSULTATION_REMINDER, CONSULTATION_FOLLOWUP -> prefs.isAppointments();
            case GROOMING_REMINDER, GROOMING_DUE -> prefs.isGrooming();
            case SYSTEM -> true;
            case PET_BIRTHDAY -> prefs.isMarketing() || true;
        };
    }

    private boolean isMedicationType(NotificationType type) {
        return type == NotificationType.MEDICATION_REMINDER 
                || type == NotificationType.MEDICATION_DOSE 
                || type == NotificationType.MEDICATION_LATE;
    }

    private boolean isTimeInDnd(LocalTime nowTime, LocalTime start, LocalTime end) {
        if (start == null || end == null || start.equals(end)) {
            return false;
        }
        if (start.isBefore(end)) {
            return !nowTime.isBefore(start) && nowTime.isBefore(end);
        } else {
            return !nowTime.isBefore(start) || nowTime.isBefore(end);
        }
    }
}
