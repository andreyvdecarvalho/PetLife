package com.petlife.modules.auth.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.application.dto.RegisterRequest;
import com.petlife.modules.auth.application.dto.TokenResponse;
import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.auth.domain.entity.UserPlan;
import com.petlife.shared.exception.BusinessException;
import com.petlife.modules.auth.application.port.PasswordEncryptionPort;
import com.petlife.modules.auth.application.port.TokenGeneratorPort;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncryptionPort passwordEncoder;
    private final TokenGeneratorPort jwtService;

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

        return new TokenResponse(
                jwtService.generateAccessToken(user),
                jwtService.generateRefreshToken(user)
        );
    }
}
