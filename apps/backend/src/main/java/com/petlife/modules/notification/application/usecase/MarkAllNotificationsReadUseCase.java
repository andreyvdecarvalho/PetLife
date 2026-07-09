package com.petlife.modules.notification.application.usecase;

import com.petlife.modules.notification.application.port.NotificationMessageRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MarkAllNotificationsReadUseCase {

    private final NotificationMessageRepositoryPort messageRepository;

    @Transactional
    public void execute(UUID userId) {
        messageRepository.markAllAsRead(userId);
    }
}
