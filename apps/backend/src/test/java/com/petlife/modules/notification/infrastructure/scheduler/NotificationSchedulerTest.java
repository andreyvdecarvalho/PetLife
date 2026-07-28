package com.petlife.modules.notification.infrastructure.scheduler;

import com.petlife.modules.notification.application.usecase.ProcessPendingEventsUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationSchedulerTest {

    @Mock
    private ProcessPendingEventsUseCase processPendingEventsUseCase;

    @InjectMocks
    private NotificationScheduler notificationScheduler;

    @Test
    @DisplayName("checkUpcomingEvents should delegate to ProcessPendingEventsUseCase")
    void checkUpcomingEvents() {
        notificationScheduler.checkUpcomingEvents();
        verify(processPendingEventsUseCase).checkUpcomingEvents();
    }

    @Test
    @DisplayName("checkLateMedications should delegate to ProcessPendingEventsUseCase")
    void checkLateMedications() {
        notificationScheduler.checkLateMedications();
        verify(processPendingEventsUseCase).checkLateMedications();
    }

    @Test
    @DisplayName("checkPetBirthdays should delegate to ProcessPendingEventsUseCase")
    void checkPetBirthdays() {
        notificationScheduler.checkPetBirthdays();
        verify(processPendingEventsUseCase).checkPetBirthdays();
    }
}
