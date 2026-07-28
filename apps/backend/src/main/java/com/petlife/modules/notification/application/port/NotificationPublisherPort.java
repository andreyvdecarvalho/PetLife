package com.petlife.modules.notification.application.port;

import com.petlife.modules.notification.application.dto.NotificationPayload;

public interface NotificationPublisherPort {
    void publish(NotificationPayload payload);
}
