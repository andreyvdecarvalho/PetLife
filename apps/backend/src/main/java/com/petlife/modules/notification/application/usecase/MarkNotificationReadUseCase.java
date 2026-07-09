package com.petlife.modules.notification.application.usecase;

import com.petlife.modules.notification.application.port.NotificationMessageRepositoryPort;
import com.petlife.modules.notification.domain.entity.NotificationMessage;
import com.petlife.modules.notification.infrastructure.dto.NotificationResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MarkNotificationReadUseCase {

    private final NotificationMessageRepositoryPort messageRepository;

    @Transactional
    public NotificationResponse execute(UUID userId, UUID notificationId) {
        NotificationMessage message = messageRepository.findById(notificationId)
                .orElseThrow(() -> BusinessException.notFound(
                        "NOTIFICATION_NOT_FOUND", "Notificação não encontrada."));

        if (!message.getUserId().equals(userId)) {
            throw BusinessException.forbidden(
                    "ACCESS_DENIED", "Esta notificação não pertence a você.");
        }

        message.setRead(true);
        NotificationMessage saved = messageRepository.save(message);
        return NotificationResponse.fromEntity(saved);
    }
}
