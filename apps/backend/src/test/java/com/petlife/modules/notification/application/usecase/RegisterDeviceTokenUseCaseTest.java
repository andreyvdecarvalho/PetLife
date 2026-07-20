package com.petlife.modules.notification.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.domain.entity.User;
import com.petlife.shared.exception.BusinessException;
import com.petlife.shared.factories.UserFactory;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegisterDeviceTokenUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private RegisterDeviceTokenUseCase registerDeviceTokenUseCase;

    @Test
    @DisplayName("Deve registrar token FCM com sucesso")
    void shouldRegisterDeviceTokenSuccessfully() {
        UUID userId = UUID.randomUUID();
        User user = UserFactory.make(u -> u.setId(userId));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        registerDeviceTokenUseCase.execute(userId, "new-fcm-token");

        assertThat(user.getFcmToken()).isEqualTo("new-fcm-token");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Deve lancar excecao quando usuario nao for encontrado")
    void shouldThrowWhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> registerDeviceTokenUseCase.execute(userId, "token"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Usuário não encontrado.");
    }
}
