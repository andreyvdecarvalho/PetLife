package com.petlife.modules.auth.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.application.dto.LoginRequest;
import com.petlife.modules.auth.application.dto.TokenResponse;
import com.petlife.shared.exception.BusinessException;
import com.petlife.modules.auth.application.port.PasswordEncryptionPort;
import com.petlife.modules.auth.application.port.TokenGeneratorPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncryptionPort passwordEncoder;
    private final TokenGeneratorPort jwtService;

    public TokenResponse execute(LoginRequest request) {
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> BusinessException.unauthorized(
                        "AUTH_INVALID_CREDENTIALS", "E-mail ou senha inválidos."));

        if (user.getDeletedAt() != null) {
            throw BusinessException.unauthorized("AUTH_INVALID_CREDENTIALS", "E-mail ou senha inválidos.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw BusinessException.unauthorized("AUTH_INVALID_CREDENTIALS", "E-mail ou senha inválidos.");
        }

        return new TokenResponse(
                jwtService.generateAccessToken(user),
                jwtService.generateRefreshToken(user)
        );
    }
}
