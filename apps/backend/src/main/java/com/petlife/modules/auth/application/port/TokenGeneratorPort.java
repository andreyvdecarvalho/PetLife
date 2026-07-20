package com.petlife.modules.auth.application.port;

import com.petlife.modules.auth.domain.entity.User;

public interface TokenGeneratorPort {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    String extractUserIdFromRefreshToken(String token);
    String generatePasswordResetToken(User user);
    String extractEmailFromResetToken(String token);
}
