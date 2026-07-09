package com.petlife.modules.notification.application.usecase;

import com.petlife.modules.notification.application.port.NotificationPreferencesRepositoryPort;
import com.petlife.modules.notification.domain.entity.NotificationPreferences;
import com.petlife.modules.notification.infrastructure.dto.NotificationPreferencesResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GetNotificationPreferencesUseCaseTest {

    @Mock
    private NotificationPreferencesRepositoryPort preferencesRepository;

    @InjectMocks
    private GetNotificationPreferencesUseCase getNotificationPreferencesUseCase;

    @Test
    @DisplayName("Deve retornar preferencias existentes")
    void shouldReturnExistingPreferences() {
        UUID userId = UUID.randomUUID();
        NotificationPreferences prefs = new NotificationPreferences();
        prefs.setUserId(userId);
        prefs.setPushEnabled(false);

        given(preferencesRepository.findByUserId(userId)).willReturn(Optional.of(prefs));

        NotificationPreferencesResponse response = getNotificationPreferencesUseCase.execute(userId);

        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.pushEnabled()).isFalse();
    }

    @Test
    @DisplayName("Deve criar e retornar preferencias default caso nao existam")
    void shouldCreateAndReturnDefaultPreferences() {
        UUID userId = UUID.randomUUID();
        given(preferencesRepository.findByUserId(userId)).willReturn(Optional.empty());
        given(preferencesRepository.save(any(NotificationPreferences.class))).willAnswer(inv -> inv.getArgument(0));

        NotificationPreferencesResponse response = getNotificationPreferencesUseCase.execute(userId);

        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.pushEnabled()).isTrue();
        verify(preferencesRepository).save(any(NotificationPreferences.class));
    }
}
