package com.petlife.modules.notification.application.usecase;

import com.petlife.modules.notification.application.port.NotificationPreferencesRepositoryPort;
import com.petlife.modules.notification.domain.entity.NotificationPreferences;
import com.petlife.modules.notification.infrastructure.dto.NotificationPreferencesRequest;
import com.petlife.modules.notification.infrastructure.dto.NotificationPreferencesResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UpdateNotificationPreferencesUseCaseTest {

    @Mock
    private NotificationPreferencesRepositoryPort preferencesRepository;

    @InjectMocks
    private UpdateNotificationPreferencesUseCase updateNotificationPreferencesUseCase;

    @Test
    @DisplayName("Deve atualizar preferencias com sucesso")
    void shouldUpdatePreferencesSuccessfully() {
        UUID userId = UUID.randomUUID();
        NotificationPreferences prefs = new NotificationPreferences();
        prefs.setUserId(userId);

        NotificationPreferencesRequest request = new NotificationPreferencesRequest(
                false, false, false, false, false, false, false,
                LocalTime.of(23, 0), LocalTime.of(6, 0)
        );

        given(preferencesRepository.findByUserId(userId)).willReturn(Optional.of(prefs));
        given(preferencesRepository.save(any(NotificationPreferences.class))).willAnswer(inv -> inv.getArgument(0));

        NotificationPreferencesResponse response = updateNotificationPreferencesUseCase.execute(userId, request);

        assertThat(response.pushEnabled()).isFalse();
        assertThat(response.emailEnabled()).isFalse();
        assertThat(response.doNotDisturbStart()).isEqualTo(LocalTime.of(23, 0));
        verify(preferencesRepository).save(prefs);
    }
}
