package com.petlife.modules.notification.infrastructure.messaging;

import com.petlife.modules.notification.application.port.NotificationPublisherPort;
import com.petlife.modules.notification.infrastructure.config.RabbitConfig;
import com.petlife.modules.notification.infrastructure.dto.NotificationPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer implements NotificationPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(NotificationPayload payload) {
        log.info("Publishing notification message to queue: {}", payload);
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY,
                payload
        );
    }
}
