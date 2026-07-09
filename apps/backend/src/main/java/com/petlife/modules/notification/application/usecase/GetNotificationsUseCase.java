package com.petlife.modules.notification.application.usecase;

import com.petlife.modules.notification.application.port.NotificationMessageRepositoryPort;
import com.petlife.modules.notification.domain.entity.NotificationMessage;
import com.petlife.modules.notification.infrastructure.dto.NotificationResponse;
import com.petlife.shared.response.ApiResponse;
import com.petlife.shared.response.PageMeta;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetNotificationsUseCase {

    private final NotificationMessageRepositoryPort messageRepository;

    @Transactional(readOnly = true)
    public ApiResponse<List<NotificationResponse>> execute(UUID userId, Pageable pageable) {
        Page<NotificationMessage> page = messageRepository.findByUserId(userId, pageable);
        List<NotificationResponse> content = page.getContent().stream()
                .map(NotificationResponse::fromEntity)
                .toList();

        PageMeta meta = new PageMeta(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return ApiResponse.paged(content, meta);
    }
}
