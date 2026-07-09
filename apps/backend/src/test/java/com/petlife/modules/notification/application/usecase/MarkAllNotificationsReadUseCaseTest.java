package com.petlife.modules.notification.application.usecase;

import com.petlife.modules.notification.application.port.NotificationMessageRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MarkAllNotificationsReadUseCaseTest {

    @Mock
    private NotificationMessageRepositoryPort messageRepository;

    @InjectMocks
    private MarkAllNotificationsReadUseCase markAllNotificationsReadUseCase;

    @Test
    @DisplayName("Deve marcar todas como lidas chamando a porta de persistencia")
    void shouldMarkAllAsRead() {
        UUID userId = UUID.randomUUID();

        markAllNotificationsReadUseCase.execute(userId);

        verify(messageRepository).markAllAsRead(userId);
    }
}
