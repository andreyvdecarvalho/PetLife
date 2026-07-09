package com.petlife.modules.notification.application.usecase;

import com.petlife.modules.notification.application.port.NotificationMessageRepositoryPort;
import com.petlife.modules.notification.domain.entity.NotificationMessage;
import com.petlife.modules.notification.domain.entity.NotificationType;
import com.petlife.modules.notification.infrastructure.dto.NotificationResponse;
import com.petlife.shared.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MarkNotificationReadUseCaseTest {

    @Mock
    private NotificationMessageRepositoryPort messageRepository;

    @InjectMocks
    private MarkNotificationReadUseCase markNotificationReadUseCase;

    @Test
    @DisplayName("Deve marcar notificação como lida com sucesso")
    void shouldMarkAsReadSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();

        NotificationMessage msg = new NotificationMessage();
        msg.setId(notificationId);
        msg.setUserId(userId);
        msg.setRead(false);
        msg.setType(NotificationType.SYSTEM);
        msg.setTitle("Title");
        msg.setBody("Body");

        given(messageRepository.findById(notificationId)).willReturn(Optional.of(msg));
        given(messageRepository.save(any(NotificationMessage.class))).willAnswer(inv -> inv.getArgument(0));

        NotificationResponse response = markNotificationReadUseCase.execute(userId, notificationId);

        assertThat(response.read()).isTrue();
        verify(messageRepository).save(msg);
    }

    @Test
    @DisplayName("Deve lancar excecao caso notificação nao pertença ao usuario")
    void shouldThrowIfNotificationDoesNotBelongToUser() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();

        NotificationMessage msg = new NotificationMessage();
        msg.setId(notificationId);
        msg.setUserId(otherUserId);

        given(messageRepository.findById(notificationId)).willReturn(Optional.of(msg));

        assertThatThrownBy(() -> markNotificationReadUseCase.execute(userId, notificationId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Esta notificação não pertence a você.");
    }
}
