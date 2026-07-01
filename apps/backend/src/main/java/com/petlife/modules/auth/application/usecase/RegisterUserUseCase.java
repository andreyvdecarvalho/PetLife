package com.petlife.modules.auth.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.dto.RegisterRequest;
import com.petlife.modules.auth.dto.TokenResponse;
import com.petlife.modules.auth.entity.User;
import com.petlife.modules.auth.entity.UserPlan;
import com.petlife.shared.exception.BusinessException;
import com.petlife.shared.security.JwtService;
import com.petlife.shared.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Use Case: Registrar novo usuário no PetLife.
 * Responsabilidade única: criação de conta + geração de tokens de acesso.
 * Princípio: Single Responsibility (SRP) — Clean Architecture.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public TokenResponse execute(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw BusinessException.conflict("AUTH_EMAIL_ALREADY_EXISTS", "E-mail já cadastrado.");
        }

        var user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setPlan(UserPlan.FREE);
        user.setEmailVerified(false);
        user.setLgpdAcceptedAt(LocalDateTime.now());

        userRepository.save(user);
        log.info("Usuário registrado com sucesso: {}", user.getEmail());

        var principal = UserPrincipal.create(user);
        return new TokenResponse(
                jwtService.generateAccessToken(principal),
                jwtService.generateRefreshToken(principal)
        );
    }
}
