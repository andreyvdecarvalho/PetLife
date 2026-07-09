package com.petlife.modules.notification.application.usecase;

import com.petlife.modules.notification.application.port.NotificationPublisherPort;
import com.petlife.modules.notification.domain.entity.NotificationType;
import com.petlife.modules.notification.infrastructure.dto.NotificationPayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EnqueueNotificationUseCaseTest {

    @Mock
    private NotificationPublisherPort publisherPort;

    @InjectMocks
    private EnqueueNotificationUseCase enqueueNotificationUseCase;

    @Test
    @DisplayName("Deve publicar payload na fila com sucesso")
    void shouldPublishPayloadToQueue() {
        NotificationPayload payload = new NotificationPayload(
                UUID.randomUUID(), NotificationType.SYSTEM, "Title", "Body", null
        );

        enqueueNotificationUseCase.execute(payload);

        verify(publisherPort).publish(payload);
    }
}
