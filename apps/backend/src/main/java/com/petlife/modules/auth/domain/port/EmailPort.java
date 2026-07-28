package com.petlife.modules.auth.domain.port;

public interface EmailPort {
    void sendVerificationEmail(String email, String token);
}
