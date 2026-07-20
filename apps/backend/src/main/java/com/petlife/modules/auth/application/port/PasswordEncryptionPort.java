package com.petlife.modules.auth.application.port;

public interface PasswordEncryptionPort {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
