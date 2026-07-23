package com.petlife.modules.notification.application.usecase;

import com.petlife.modules.notification.application.port.NotificationMessageRepositoryPort;
import com.petlife.modules.notification.domain.entity.NotificationMessage;
import com.petlife.modules.notification.domain.entity.NotificationType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        NotificationMessage msg = new NotificationMessage();
        msg.setId(UUID.randomUUID());
        msg.setUserId(userId);
        msg.setTitle("Test Title");
        msg.setBody("Test Body");
        msg.setType(NotificationType.SYSTEM);

        var pagedResult = new com.petlife.modules.notification.application.usecase.PagedResult<>(
                List.of(msg),
                new com.petlife.shared.response.PageMeta(0, 10, 1L, 1)
        );
        given(messageRepository.findByUserId(userId, 0, 10)).willReturn(pagedResult);

        var response = getNotificationsUseCase.execute(userId, 0, 10);

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).title()).isEqualTo("Test Title");
        assertThat(response.meta().total()).isEqualTo(1);
    }
}
