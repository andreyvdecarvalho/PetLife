package com.petlife.modules.auth.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.application.dto.RefreshTokenRequest;
import com.petlife.modules.auth.application.dto.TokenResponse;
import com.petlife.shared.exception.BusinessException;
import com.petlife.modules.auth.application.port.TokenGeneratorPort;
import lombok.RequiredArgsConstructor;
import java.util.UUID;

@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private final UserRepositoryPort userRepository;
    private final TokenGeneratorPort tokenService;

    public TokenResponse execute(RefreshTokenRequest request) {
        String userIdStr = tokenService.extractUserIdFromRefreshToken(request.refreshToken());
        
        if (userIdStr == null) {
            throw BusinessException.unauthorized("AUTH_INVALID_REFRESH_TOKEN", "Refresh token inválido ou expirado.");
        }

        UUID userId = UUID.fromString(userIdStr);
        var user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.unauthorized("AUTH_INVALID_REFRESH_TOKEN", "Usuário não encontrado."));

        if (user.getDeletedAt() != null) {
            throw BusinessException.unauthorized("AUTH_INVALID_REFRESH_TOKEN", "Usuário não encontrado.");
        }

        return new TokenResponse(
                tokenService.generateAccessToken(user),
                tokenService.generateRefreshToken(user)
        );
    }
}
