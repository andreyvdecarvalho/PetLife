package com.petlife.modules.auth.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.dto.LoginRequest;
import com.petlife.modules.auth.dto.TokenResponse;
import com.petlife.shared.exception.BusinessException;
import com.petlife.shared.security.JwtService;
import com.petlife.shared.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: Autenticar usuário com e-mail e senha.
 * Responsabilidade única: validar credenciais + gerar tokens JWT.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoginUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
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

        log.info("Usuário autenticado com sucesso: {}", user.getEmail());

        var principal = UserPrincipal.create(user);
        return new TokenResponse(
                jwtService.generateAccessToken(principal),
                jwtService.generateRefreshToken(principal)
        );
    }
}
