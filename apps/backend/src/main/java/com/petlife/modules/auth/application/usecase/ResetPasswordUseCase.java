package com.petlife.modules.auth.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.application.dto.ResetPasswordRequest;
import com.petlife.modules.auth.application.port.PasswordEncryptionPort;
import com.petlife.modules.auth.application.port.TokenGeneratorPort;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResetPasswordUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncryptionPort passwordEncoder;
    private final TokenGeneratorPort tokenService;

    public void execute(ResetPasswordRequest request) {
        String email = tokenService.extractEmailFromResetToken(request.token());
        
        if (email == null) {
            throw BusinessException.badRequest("AUTH_INVALID_RESET_TOKEN", "Token inválido ou expirado.");
        }

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> BusinessException.badRequest("AUTH_INVALID_RESET_TOKEN", "Usuário não encontrado."));

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
}
