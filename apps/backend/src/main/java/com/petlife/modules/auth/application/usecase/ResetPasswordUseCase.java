package com.petlife.modules.auth.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.dto.ResetPasswordRequest;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: Redefinir senha usando token JWT de recuperação.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ResetPasswordUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtDecoder jwtDecoder;

    @Transactional
    public void execute(ResetPasswordRequest request) {
        try {
            var jwt = jwtDecoder.decode(request.token());
            var action = (String) jwt.getClaim("action");

            if (!"reset-password".equals(action)) {
                throw BusinessException.badRequest("AUTH_INVALID_TOKEN", "Token de redefinição inválido.");
            }

            var email = jwt.getSubject();
            var user = userRepository.findByEmail(email)
                    .orElseThrow(() -> BusinessException.notFound("USER_NOT_FOUND", "Usuário não encontrado."));

            user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
            userRepository.save(user);

            log.info("Senha redefinida com sucesso para o usuário: {}", email);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            log.warn("Falha ao redefinir senha: {}", ex.getMessage());
            throw BusinessException.badRequest("AUTH_INVALID_TOKEN", "Token de redefinição inválido ou expirado.");
        }
    }
}
