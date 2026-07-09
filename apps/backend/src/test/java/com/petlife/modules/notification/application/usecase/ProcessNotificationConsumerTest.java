package com.petlife.modules.notification.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.entity.User;
import com.petlife.modules.notification.application.port.FcmServicePort;
import com.petlife.modules.notification.application.port.NotificationMessageRepositoryPort;
import com.petlife.modules.notification.application.port.NotificationPreferencesRepositoryPort;
import com.petlife.modules.notification.domain.entity.NotificationMessage;
import com.petlife.modules.notification.domain.entity.NotificationPreferences;
import com.petlife.modules.notification.domain.entity.NotificationType;
import com.petlife.modules.notification.infrastructure.dto.NotificationPayload;
import com.petlife.shared.factories.UserFactory;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProcessNotificationConsumerTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private NotificationPreferencesRepositoryPort preferencesRepository;
    @Mock
    private NotificationMessageRepositoryPort messageRepository;
    @Mock
    private FcmServicePort fcmService;

    @InjectMocks
    private ProcessNotificationConsumer processNotificationConsumer;

    private User user;
    private NotificationPreferences prefs;

    @BeforeEach
    void setUp() {
        user = UserFactory.make(u -> {
            u.setId(UUID.randomUUID());
            u.setFcmToken("dummy-fcm-token");
        });
        prefs = new NotificationPreferences();
        prefs.setUserId(user.getId());
    }

    @Test
    @DisplayName("Deve processar, persistir e enviar push notification de vacina com sucesso")
    void shouldProcessAndPersistAndSendPushForVaccine() {
        NotificationPayload payload = new NotificationPayload(
                user.getId(), NotificationType.VACCINATION_DUE, "Lembrete", "Corpo", null
        );

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(preferencesRepository.findByUserId(user.getId())).willReturn(Optional.of(prefs));

        processNotificationConsumer.execute(payload);

        verify(messageRepository).save(any(NotificationMessage.class));
        verify(fcmService).sendPushNotification("dummy-fcm-token", "Lembrete", "Corpo");
    }

    @Test
    @DisplayName("Deve descartar notificação se categoria estiver desativada")
    void shouldDiscardIfCategoryIsDisabled() {
        NotificationPayload payload = new NotificationPayload(
                user.getId(), NotificationType.VACCINATION_DUE, "Lembrete", "Corpo", null
        );
        prefs.setVaccines(false);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(preferencesRepository.findByUserId(user.getId())).willReturn(Optional.of(prefs));

        processNotificationConsumer.execute(payload);

        verify(messageRepository, never()).save(any(NotificationMessage.class));
        verify(fcmService, never()).sendPushNotification(any(), any(), any());
    }

    @Test
    @DisplayName("Deve descartar se estiver em horario de nao perturbe")
    void shouldDiscardIfInDndWindow() {
        NotificationPayload payload = new NotificationPayload(
                user.getId(), NotificationType.VACCINATION_DUE, "Lembrete", "Corpo", null
        );
        prefs.setDoNotDisturbStart(LocalTime.MIN);
        prefs.setDoNotDisturbEnd(LocalTime.MAX);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(preferencesRepository.findByUserId(user.getId())).willReturn(Optional.of(prefs));

        processNotificationConsumer.execute(payload);

        verify(messageRepository, never()).save(any(NotificationMessage.class));
    }

    @Test
    @DisplayName("Deve descartar se limite diario nao-medicamento de 5 for atingido")
    void shouldDiscardIfDailyLimitExceeded() {
        NotificationPayload payload = new NotificationPayload(
                user.getId(), NotificationType.VACCINATION_DUE, "Lembrete", "Corpo", null
        );

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(preferencesRepository.findByUserId(user.getId())).willReturn(Optional.of(prefs));
        given(messageRepository.countByUserIdAndCreatedAtAfterAndTypeNotIn(eq(user.getId()), any(), any()))
                .willReturn(5L);

        processNotificationConsumer.execute(payload);

        verify(messageRepository, never()).save(any(NotificationMessage.class));
    }

    @Test
    @DisplayName("Nao deve descartar se limite diario for atingido mas for tipo medicamento")
    void shouldNotDiscardIfDailyLimitExceededButIsMedication() {
        NotificationPayload payload = new NotificationPayload(
                user.getId(), NotificationType.MEDICATION_DOSE, "Med", "Corpo", null
        );

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(preferencesRepository.findByUserId(user.getId())).willReturn(Optional.of(prefs));

        processNotificationConsumer.execute(payload);

        verify(messageRepository).save(any(NotificationMessage.class));
        verify(fcmService).sendPushNotification("dummy-fcm-token", "Med", "Corpo");
    }
}
