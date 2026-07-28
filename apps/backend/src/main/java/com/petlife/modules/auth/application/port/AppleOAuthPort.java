package com.petlife.modules.auth.application.port;

public interface AppleOAuthPort {
    AppleUserInfo getAppleUserInfo(String idToken);

    record AppleUserInfo(String email) {}
}
