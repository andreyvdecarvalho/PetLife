package com.petlife.modules.notification.application.usecase;

import com.petlife.modules.notification.application.port.NotificationMessageRepositoryPort;
import com.petlife.modules.notification.domain.entity.NotificationMessage;
import com.petlife.modules.notification.domain.entity.NotificationType;
import com.petlife.modules.notification.infrastructure.dto.NotificationResponse;
import com.petlife.shared.response.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GetNotificationsUseCaseTest {

    @Mock
    private NotificationMessageRepositoryPort messageRepository;

    @InjectMocks
    private GetNotificationsUseCase getNotificationsUseCase;

    @Test
    @DisplayName("Deve obter lista paginada de notificacoes")
    void shouldGetPagedNotifications() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        NotificationMessage msg = new NotificationMessage();
        msg.setId(UUID.randomUUID());
        msg.setUserId(userId);
        msg.setTitle("Test Title");
        msg.setBody("Test Body");
        msg.setType(NotificationType.SYSTEM);

        Page<NotificationMessage> page = new PageImpl<>(List.of(msg), pageable, 1);
        given(messageRepository.findByUserId(userId, pageable)).willReturn(page);

        ApiResponse<List<NotificationResponse>> response = getNotificationsUseCase.execute(userId, pageable);

        assertThat(response.data()).hasSize(1);
        assertThat(response.data().get(0).title()).isEqualTo("Test Title");
        assertThat(response.meta().total()).isEqualTo(1);
    }
}
