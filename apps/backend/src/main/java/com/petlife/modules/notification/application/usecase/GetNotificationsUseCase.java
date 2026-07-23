package com.petlife.modules.notification.application.usecase;

import com.petlife.modules.notification.application.port.NotificationMessageRepositoryPort;
import com.petlife.modules.notification.infrastructure.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetNotificationsUseCase {

    private final NotificationMessageRepositoryPort messageRepository;

    @Transactional(readOnly = true)
    public PagedResult<NotificationResponse> execute(UUID userId, int page, int size) {
        PagedResult<com.petlife.modules.notification.domain.entity.NotificationMessage> domainPage =
                messageRepository.findByUserId(userId, page, size);

        List<NotificationResponse> content = domainPage.content().stream()
                .map(NotificationResponse::fromEntity)
                .toList();

        return new PagedResult<>(content, domainPage.meta());
    }
}
