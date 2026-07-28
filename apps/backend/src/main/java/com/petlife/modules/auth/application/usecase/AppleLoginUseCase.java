package com.petlife.modules.auth.application.usecase;

import com.petlife.modules.auth.application.dto.AppleLoginRequest;
import com.petlife.modules.auth.application.dto.TokenResponse;
import com.petlife.modules.auth.application.port.AppleOAuthPort;
import com.petlife.modules.auth.application.port.TokenGeneratorPort;
import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.auth.domain.entity.UserPlan;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class AppleLoginUseCase {
    private final UserRepositoryPort userRepository;
    private final TokenGeneratorPort jwtService;
    private final AppleOAuthPort appleOAuthPort;

    public TokenResponse execute(AppleLoginRequest request) {
        var appleInfo = appleOAuthPort.getAppleUserInfo(request.idToken());

        String email = appleInfo.email() != null ? appleInfo.email() : request.email();
        String name = request.name() != null && !request.name().isBlank() ? request.name() : "Tutor Apple";

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("E-mail não fornecido pela Apple ou requisição");
        }

        var user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setPlan(UserPlan.FREE);
            user.setEmailVerified(true);
            user.setLgpdAcceptedAt(LocalDateTime.now());
            userRepository.save(user);
        } else {
            if (user.getDeletedAt() != null) {
                user.setDeletedAt(null);
                userRepository.save(user);
            }
        }

        return new TokenResponse(
                jwtService.generateAccessToken(user),
                jwtService.generateRefreshToken(user)
        );
    }
}
