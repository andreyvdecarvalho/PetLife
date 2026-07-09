package com.petlife.modules.notification.infrastructure.messaging;

import com.petlife.modules.notification.application.usecase.ProcessNotificationConsumer;
import com.petlife.modules.notification.infrastructure.config.RabbitConfig;
import com.petlife.modules.notification.infrastructure.dto.NotificationPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final ProcessNotificationConsumer processNotificationConsumer;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void consume(NotificationPayload payload) {
        log.info("Received notification payload from queue: {}", payload);
        try {
            processNotificationConsumer.execute(payload);
        } catch (Exception e) {
            log.error("Error processing notification payload from queue", e);
        }
    }
}
