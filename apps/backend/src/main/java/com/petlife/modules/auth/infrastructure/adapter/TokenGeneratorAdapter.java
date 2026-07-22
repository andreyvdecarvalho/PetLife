package com.petlife.modules.auth.infrastructure.adapter;

import com.petlife.modules.auth.application.port.TokenGeneratorPort;
import com.petlife.modules.auth.domain.entity.User;
import com.petlife.shared.security.JwtService;
import com.petlife.shared.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TokenGeneratorAdapter implements TokenGeneratorPort {

    private final JwtService jwtService;
    private final JwtDecoder jwtDecoder;

    @Override
    public String generateAccessToken(User user) {
        return jwtService.generateAccessToken(UserPrincipal.create(user));
    }

    @Override
    public String generateRefreshToken(User user) {
        return jwtService.generateRefreshToken(UserPrincipal.create(user));
    }

    @Override
    public String extractUserIdFromRefreshToken(String token) {
        try {
            var decodedJwt = jwtDecoder.decode(token);
            Boolean isRefresh = decodedJwt.getClaim("refresh");
            if (isRefresh == null || !isRefresh) {
                return null;
            }
            return decodedJwt.getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    @Override
    public String generatePasswordResetToken(User user) {
        return jwtService.generatePasswordResetToken(user.getEmail());
    }

    @Override
    public String extractEmailFromResetToken(String token) {
        try {
            var decodedJwt = jwtDecoder.decode(token);
            String action = decodedJwt.getClaim("action");
            if (!"reset-password".equals(action)) {
                return null;
            }
            return decodedJwt.getSubject();
        } catch (JwtException e) {
            return null;
        }
    }
}
