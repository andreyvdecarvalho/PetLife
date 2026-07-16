package com.petlife.modules.auth.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.dto.RefreshTokenRequest;
import com.petlife.modules.auth.dto.TokenResponse;
import com.petlife.shared.exception.BusinessException;
import com.petlife.shared.security.JwtService;
import com.petlife.shared.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use Case: Atualizar token de acesso usando o refresh token.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenUseCase {

    private final UserRepositoryPort userRepository;
    private final JwtService jwtService;
    private final JwtDecoder jwtDecoder;

    @Transactional(readOnly = true)
    public TokenResponse execute(RefreshTokenRequest request) {
        Jwt jwt;
        try {
            jwt = jwtDecoder.decode(request.refreshToken());
        } catch (Exception e) {
            log.warn("Falha ao decodificar refresh token: {}", e.getMessage());
            throw BusinessException.unauthorized("AUTH_INVALID_TOKEN", "Refresh token inválido ou expirado.");
        }

        Boolean isRefresh = jwt.getClaim("refresh");
        if (isRefresh == null || !isRefresh) {
            log.warn("Token fornecido não é um refresh token válido.");
            throw BusinessException.unauthorized("AUTH_INVALID_TOKEN", "Token fornecido não é um refresh token.");
        }

        UUID userId;
        try {
            userId = UUID.fromString(jwt.getSubject());
        } catch (Exception e) {
            throw BusinessException.unauthorized("AUTH_INVALID_TOKEN", "Subject inválido no token.");
        }

        var user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Refresh token para usuário inexistente: {}", userId);
                    return BusinessException.unauthorized("AUTH_INVALID_USER", "Usuário não encontrado.");
                });

        if (user.getDeletedAt() != null) {
            log.warn("Refresh token para usuário deletado: {}", userId);
            throw BusinessException.unauthorized("AUTH_INVALID_USER", "Conta excluída.");
        }

        log.info("Sessão renovada com sucesso para: {}", user.getEmail());

        var principal = UserPrincipal.create(user);
        return new TokenResponse(
                jwtService.generateAccessToken(principal),
                jwtService.generateRefreshToken(principal)
        );
    }
}
