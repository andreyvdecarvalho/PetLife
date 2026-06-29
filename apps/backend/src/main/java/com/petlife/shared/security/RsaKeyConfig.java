package com.petlife.shared.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@Slf4j
public class RsaKeyConfig {

    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;

    public RsaKeyConfig() {
        try {
            log.info("Generating ephemeral RSA 2048 key pair for JWT signing...");
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.publicKey = (RSAPublicKey) keyPair.getPublic();
            this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
            log.info("RSA key pair generated successfully.");
        } catch (Exception ex) {
            log.error("Failed to generate RSA key pair", ex);
            throw new IllegalStateException("Could not initialize RSA keys", ex);
        }
    }

    @Bean
    public RSAPublicKey publicKey() {
        return publicKey;
    }

    @Bean
    public RSAPrivateKey privateKey() {
        return privateKey;
    }
}
