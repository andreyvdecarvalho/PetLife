package com.petlife.modules.notification.infrastructure.scheduler;

import com.petlife.modules.notification.application.usecase.ProcessPendingEventsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final ProcessPendingEventsUseCase processPendingEventsUseCase;

    @Scheduled(cron = "0 0 * * * *")
    public void checkUpcomingEvents() {
        log.info("Running scheduled check for upcoming events");
        processPendingEventsUseCase.checkUpcomingEvents();
    }

    @Scheduled(cron = "0 */30 * * * *")
    public void checkLateMedications() {
        log.info("Running scheduled check for late medications");
        processPendingEventsUseCase.checkLateMedications();
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void checkPetBirthdays() {
        log.info("Running scheduled check for pet birthdays");
        processPendingEventsUseCase.checkPetBirthdays();
    }
}
