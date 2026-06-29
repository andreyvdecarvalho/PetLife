package com.petlife.modules.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petlife.modules.auth.dto.ForgotPasswordRequest;
import com.petlife.modules.auth.dto.GoogleLoginRequest;
import com.petlife.modules.auth.dto.LoginRequest;
import com.petlife.modules.auth.dto.RegisterRequest;
import com.petlife.modules.auth.dto.ResetPasswordRequest;
import com.petlife.modules.auth.dto.TokenResponse;
import com.petlife.modules.auth.dto.UpdateProfileRequest;
import com.petlife.modules.auth.dto.UserResponse;
import com.petlife.modules.auth.entity.User;
import com.petlife.modules.auth.entity.UserPlan;
import com.petlife.modules.auth.repository.UserRepository;
import com.petlife.shared.exception.BusinessException;
import com.petlife.shared.security.JwtService;
import com.petlife.shared.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final ObjectMapper objectMapper;

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw BusinessException.conflict("AUTH_EMAIL_ALREADY_EXISTS", "E-mail já cadastrado.");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setPlan(UserPlan.FREE);
        user.setEmailVerified(false);
        user.setLgpdAcceptedAt(LocalDateTime.now());

        userRepository.save(user);
        log.info("User registered successfully: {}", user.getEmail());

        UserPrincipal principal = UserPrincipal.create(user);
        return new TokenResponse(
                jwtService.generateAccessToken(principal),
                jwtService.generateRefreshToken(principal)
        );
    }


    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> BusinessException.unauthorized("AUTH_INVALID_CREDENTIALS", "E-mail ou senha inválidos."));

        if (user.getDeletedAt() != null) {
            throw BusinessException.unauthorized("AUTH_INVALID_CREDENTIALS", "E-mail ou senha inválidos.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw BusinessException.unauthorized("AUTH_INVALID_CREDENTIALS", "E-mail ou senha inválidos.");
        }

        log.info("User logged in successfully: {}", user.getEmail());

        UserPrincipal principal = UserPrincipal.create(user);
        return new TokenResponse(
                jwtService.generateAccessToken(principal),
                jwtService.generateRefreshToken(principal)
        );
    }

    @Transactional
    public TokenResponse loginWithGoogle(GoogleLoginRequest request) {
        try {
            String[] parts = request.idToken().split("\\.");
            if (parts.length < 2) {
                throw BusinessException.badRequest("AUTH_INVALID_GOOGLE_TOKEN", "Token do Google inválido.");
            }
            String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            var payload = objectMapper.readTree(payloadJson);

            String email = payload.path("email").asText();
            String name = payload.path("name").asText();
            String picture = payload.path("picture").asText();

            if (email == null || email.isBlank()) {
                throw BusinessException.badRequest("AUTH_INVALID_GOOGLE_TOKEN", "Token do Google não contém e-mail.");
            }

            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setName(name != null && !name.isBlank() ? name : "Tutor Google");
                user.setAvatarUrl(picture);
                user.setPlan(UserPlan.FREE);
                user.setEmailVerified(true);
                user.setLgpdAcceptedAt(LocalDateTime.now());
                userRepository.save(user);
                log.info("New user registered via Google: {}", email);
            } else {
                if (user.getDeletedAt() != null) {
                    user.setDeletedAt(null);
                }
                if (picture != null && !picture.isBlank()) {
                    user.setAvatarUrl(picture);
                }
                userRepository.save(user);
                log.info("User logged in via Google: {}", email);
            }

            UserPrincipal principal = UserPrincipal.create(user);
            return new TokenResponse(
                    jwtService.generateAccessToken(principal),
                    jwtService.generateRefreshToken(principal)
            );
        } catch (Exception ex) {
            log.warn("Google login failed: {}", ex.getMessage());
            throw BusinessException.badRequest("AUTH_INVALID_GOOGLE_TOKEN", "Falha ao autenticar com Google.");
        }
    }

    @Transactional
    public void deleteAccount(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("USER_NOT_FOUND", "Usuário não encontrado."));

        // Cascade delete físico dos pets e dados relacionados conforme exigido pela LGPD
        userRepository.delete(user);
        log.info("User account and all related data deleted (LGPD compliance) for ID: {}", userId);
    }

    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("USER_NOT_FOUND", "Usuário não encontrado."));

        if (!user.getEmail().equalsIgnoreCase(request.email()) && userRepository.existsByEmail(request.email())) {
            throw BusinessException.conflict("AUTH_EMAIL_ALREADY_EXISTS", "O novo e-mail informado já está em uso.");
        }

        user.setName(request.name());
        user.setEmail(request.email());
        user.setAvatarUrl(request.avatarUrl());
        user.setTimezone(request.timezone());

        userRepository.save(user);
        log.info("User profile updated for ID: {}", userId);

        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("USER_NOT_FOUND", "Usuário não encontrado."));
        return toResponse(user);
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElse(null);

        if (user == null) {
            // Retorno silencioso por motivos de segurança (para evitar enumeração de usuários)
            log.info("Password recovery requested for non-existing email: {}", request.email());
            return;
        }

        // Gera um token curto (15 minutos) de recuperação
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("petlife")
                .issuedAt(now)
                .expiresAt(now.plus(15, ChronoUnit.MINUTES))
                .subject(user.getEmail())
                .claim("action", "reset-password")
                .build();

        String resetToken = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        // No MVP, logamos o link. Em produção, isso iria por e-mail (RabbitMQ -> NotificationService -> E-mail).
        log.info("=== RECUPERAÇÃO DE SENHA ===");
        log.info("Tutor: {}", user.getName());
        log.info("Link: http://localhost:5173/reset-password?token={}", resetToken);
        log.info("============================");
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        try {
            var jwt = jwtDecoder.decode(request.token());
            String action = jwt.getClaim("action");
            if (!"reset-password".equals(action)) {
                throw BusinessException.badRequest("AUTH_INVALID_TOKEN", "Token de redefinição inválido.");
            }

            String email = jwt.getSubject();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> BusinessException.notFound("USER_NOT_FOUND", "Usuário não encontrado."));

            user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
            userRepository.save(user);

            log.info("Password reset successfully for user: {}", email);
        } catch (Exception ex) {
            log.warn("Failed to reset password: {}", ex.getMessage());
            throw BusinessException.badRequest("AUTH_INVALID_TOKEN", "Token de redefinição inválido ou expirado.");
        }
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getTimezone(),
                user.getPlan(),
                user.isEmailVerified(),
                user.getCreatedAt()
        );
    }
}
