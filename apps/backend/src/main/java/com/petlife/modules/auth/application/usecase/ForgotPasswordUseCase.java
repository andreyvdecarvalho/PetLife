package com.petlife.modules.auth.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.dto.ForgotPasswordRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Use Case: Solicitar recuperação de senha.
 * Gera token JWT de curta duração e loga o link (MVP).
 * Em produção: enviar via RabbitMQ -> NotificationService -> e-mail.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ForgotPasswordUseCase {

    private final UserRepositoryPort userRepository;
    private final JwtEncoder jwtEncoder;

    public void execute(ForgotPasswordRequest request) {
        var user = userRepository.findByEmail(request.email()).orElse(null);

        if (user == null) {
            // Retorno silencioso por segurança (evita enumeração de usuários)
            log.info("Recuperação de senha solicitada para e-mail inexistente: {}", request.email());
            return;
        }

        var now = Instant.now();
        var claims = JwtClaimsSet.builder()
                .issuer("petlife")
                .issuedAt(now)
                .expiresAt(now.plus(15, ChronoUnit.MINUTES))
                .subject(user.getEmail())
                .claim("action", "reset-password")
                .build();

        var resetToken = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        // MVP: log do link. Produção: RabbitMQ -> NotificationService -> e-mail
        log.info("=== RECUPERAÇÃO DE SENHA ===");
        log.info("Tutor: {}", user.getName());
        log.info("Link: http://localhost:5173/reset-password?token={}", resetToken);
        log.info("============================");
    }
}
