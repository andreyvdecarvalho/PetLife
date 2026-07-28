package com.petlife.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Configuração de JWT (Encoder + Decoder) — separada por SRP.
 */
@Configuration
@RequiredArgsConstructor
@org.springframework.boot.context.properties.EnableConfigurationProperties(RsaKeyProperties.class)
public class JwtConfig {

    private final RsaKeyProperties rsaKeys;

    @Bean
    public RSAPublicKey rsaPublicKey() throws Exception {
        byte[] keyBytes = decodeKeyBytes(rsaKeys.publicKey(), "-----BEGIN PUBLIC KEY-----", "-----END PUBLIC KEY-----");
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    @Bean
    public RSAPrivateKey rsaPrivateKey() throws Exception {
        byte[] keyBytes = decodeKeyBytes(
                rsaKeys.privateKey(), "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----");
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey publicKey) {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    @Bean
    public JwtEncoder jwtEncoder(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        JWK jwk = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    private byte[] decodeKeyBytes(String input, String header, String footer) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("RSA key property is null or empty");
        }
        String cleaned = input.replaceAll("\\s", "");
        // First try to base64 decode
        byte[] decoded = Base64.getDecoder().decode(cleaned);
        String decodedStr = new String(decoded, StandardCharsets.UTF_8);
        if (decodedStr.contains(header)) {
            String pemContent = decodedStr
                .replace(header, "")
                .replace(footer, "")
                .replaceAll("\\s", "");
            return Base64.getDecoder().decode(pemContent);
        } else {
            String inputStr = input.trim();
            if (inputStr.contains(header)) {
                String pemContent = inputStr
                    .replace(header, "")
                    .replace(footer, "")
                    .replaceAll("\\s", "");
                return Base64.getDecoder().decode(pemContent);
            }
            return decoded;
        }
    }
}
