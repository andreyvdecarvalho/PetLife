package com.petlife.modules.auth.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.application.dto.UserResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import java.util.UUID;

/**
 * Use Case: Obter perfil do usuário autenticado.
 */
@RequiredArgsConstructor
public class GetUserProfileUseCase {

    private final UserRepositoryPort userRepository;

        public UserResponse execute(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("USER_NOT_FOUND", "Usuário não encontrado."));
        return UserResponse.from(user);
    }
}
