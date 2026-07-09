package com.petlife.modules.notification.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegisterDeviceTokenUseCase {

    private final UserRepositoryPort userRepository;

    @Transactional
    public void execute(UUID userId, String fcmToken) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("USER_NOT_FOUND", "Usuário não encontrado."));
        user.setFcmToken(fcmToken);
        userRepository.save(user);
    }
}
