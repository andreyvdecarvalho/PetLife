package com.petlife.modules.auth.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.application.dto.ForgotPasswordRequest;
import com.petlife.modules.auth.application.port.TokenGeneratorPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ForgotPasswordUseCase {

    private final UserRepositoryPort userRepository;
    private final TokenGeneratorPort tokenService;

    public void execute(ForgotPasswordRequest request) {
        var user = userRepository.findByEmail(request.email()).orElse(null);

        if (user == null) {
            return;
        }

        String resetToken = tokenService.generatePasswordResetToken(user);
    }
}
