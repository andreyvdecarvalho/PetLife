package com.petlife.modules.auth.application.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.dto.GoogleLoginRequest;
import com.petlife.modules.auth.dto.TokenResponse;
import com.petlife.modules.auth.entity.User;
import com.petlife.modules.auth.entity.UserPlan;
import com.petlife.shared.exception.BusinessException;
import com.petlife.shared.security.JwtService;
import com.petlife.shared.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Use Case: Autenticar ou cadastrar usuário via Google OAuth2.
 * Responsabilidade única: processar token Google ID + criar/atualizar conta.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoginWithGoogleUseCase {

    private final UserRepositoryPort userRepository;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Transactional
    public TokenResponse execute(GoogleLoginRequest request) {
        try {
            var parts = request.idToken().split("\\.");
            if (parts.length < 2) {
                throw BusinessException.badRequest("AUTH_INVALID_GOOGLE_TOKEN", "Token do Google inválido.");
            }

            var payloadJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]), java.nio.charset.StandardCharsets.UTF_8);
            var payload = objectMapper.readTree(payloadJson);

            var email = payload.path("email").asText();
            var name = payload.path("name").asText();
            var picture = payload.path("picture").asText();

            if (email == null || email.isBlank()) {
                throw BusinessException.badRequest("AUTH_INVALID_GOOGLE_TOKEN", "Token do Google não contém e-mail.");
            }

            var user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setName(name != null && !name.isBlank() ? name : "Tutor Google");
                user.setAvatarUrl(picture);
                user.setPlan(UserPlan.FREE);
                user.setEmailVerified(true);
                user.setLgpdAcceptedAt(LocalDateTime.now());
                userRepository.save(user);
                log.info("Novo usuário registrado via Google: {}", email);
            } else {
                if (user.getDeletedAt() != null) {
                    user.setDeletedAt(null);
                }
                if (picture != null && !picture.isBlank()) {
                    user.setAvatarUrl(picture);
                }
                userRepository.save(user);
                log.info("Usuário autenticado via Google: {}", email);
            }

            var principal = UserPrincipal.create(user);
            return new TokenResponse(
                    jwtService.generateAccessToken(principal),
                    jwtService.generateRefreshToken(principal)
            );
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            log.warn("Falha no login Google: {}", ex.getMessage());
            throw BusinessException.badRequest("AUTH_INVALID_GOOGLE_TOKEN", "Falha ao autenticar com Google.");
        }
    }
}
