package com.petlife.modules.auth.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.application.dto.GoogleLoginRequest;
import com.petlife.modules.auth.application.dto.TokenResponse;
import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.auth.domain.entity.UserPlan;

import com.petlife.modules.auth.application.port.OAuthProviderPort;
import com.petlife.modules.auth.application.port.TokenGeneratorPort;
import com.petlife.modules.auth.application.port.OAuthProviderPort.GoogleUserInfo;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class LoginWithGoogleUseCase {

    private final UserRepositoryPort userRepository;
    private final TokenGeneratorPort jwtService;
    private final OAuthProviderPort googleOAuthService;

    public TokenResponse execute(GoogleLoginRequest request) {
        GoogleUserInfo googleInfo = googleOAuthService.getGoogleUserInfo(request.idToken());

        var user = userRepository.findByEmail(googleInfo.email()).orElse(null);

        if (user == null) {
            user = new User();
            user.setEmail(googleInfo.email());
            user.setName(googleInfo.name() != null && !googleInfo.name().isBlank() ? googleInfo.name() : "Tutor Google");
            user.setAvatarUrl(googleInfo.avatarUrl());
            user.setPlan(UserPlan.FREE);
            user.setEmailVerified(true);
            user.setLgpdAcceptedAt(LocalDateTime.now());
            userRepository.save(user);
        } else {
            if (user.getDeletedAt() != null) {
                user.setDeletedAt(null);
            }
            if (googleInfo.avatarUrl() != null && !googleInfo.avatarUrl().isBlank()) {
                user.setAvatarUrl(googleInfo.avatarUrl());
            }
            userRepository.save(user);
        }

        return new TokenResponse(
                jwtService.generateAccessToken(user),
                jwtService.generateRefreshToken(user)
        );
    }
}
