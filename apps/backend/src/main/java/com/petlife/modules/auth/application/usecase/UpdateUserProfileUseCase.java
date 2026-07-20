package com.petlife.modules.auth.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.application.dto.UpdateProfileRequest;
import com.petlife.modules.auth.application.dto.UserResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.UUID;

/**
 * Use Case: Atualizar perfil do usuário autenticado.
 */
@RequiredArgsConstructor
@Slf4j
public class UpdateUserProfileUseCase {

    private final UserRepositoryPort userRepository;

        public UserResponse execute(UUID userId, UpdateProfileRequest request) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("USER_NOT_FOUND", "Usuário não encontrado."));

        if (!user.getEmail().equalsIgnoreCase(request.email())
                && userRepository.existsByEmail(request.email())) {
            throw BusinessException.conflict(
                    "AUTH_EMAIL_ALREADY_EXISTS", "O novo e-mail informado já está em uso.");
        }

        user.setName(request.name());
        user.setNickname(request.nickname());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setAvatarUrl(request.avatarUrl());
        user.setTimezone(request.timezone());

        userRepository.save(user);
        log.info("Perfil atualizado para usuário ID: {}", userId);

        return UserResponse.from(user);
    }
}
