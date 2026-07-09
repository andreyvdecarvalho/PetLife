package com.petlife.modules.notification.application.usecase;

import com.petlife.modules.notification.application.port.NotificationPublisherPort;
import com.petlife.modules.notification.infrastructure.dto.NotificationPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnqueueNotificationUseCase {

    private final NotificationPublisherPort publisherPort;

    public void execute(NotificationPayload payload) {
        publisherPort.publish(payload);
    }
}
