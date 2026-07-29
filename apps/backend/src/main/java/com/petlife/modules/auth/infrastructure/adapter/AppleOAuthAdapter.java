package com.petlife.modules.auth.infrastructure.adapter;

import com.petlife.modules.auth.application.port.AppleOAuthPort;
import com.petlife.shared.exception.BusinessException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class AppleOAuthAdapter implements AppleOAuthPort {

    private final JwtDecoder jwtDecoder;

    public AppleOAuthAdapter() {
        this.jwtDecoder = NimbusJwtDecoder.withJwkSetUri("https://appleid.apple.com/auth/keys").build();
    }

    @Override
    public AppleUserInfo getAppleUserInfo(String idToken) {
        try {
            Jwt jwt = jwtDecoder.decode(idToken);
            String email = jwt.getClaimAsString("email");
            return new AppleUserInfo(email);
        } catch (Exception e) {
            throw BusinessException.unauthorized("INVALID_APPLE_TOKEN", 
                "Falha ao validar o token com a Apple: " + e.getMessage());
        }
    }
}
