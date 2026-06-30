package com.petlife.modules.auth.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.dto.UserResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use Case: Obter perfil do usuário autenticado.
 */
@Component
@RequiredArgsConstructor
public class GetUserProfileUseCase {

    private final UserRepositoryPort userRepository;

    @Transactional(readOnly = true)
    public UserResponse execute(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("USER_NOT_FOUND", "Usuário não encontrado."));
        return UserResponse.from(user);
    }
}
