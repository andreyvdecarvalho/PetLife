package com.petlife.modules.auth.application.port;

public interface OAuthProviderPort {
    GoogleUserInfo getGoogleUserInfo(String idToken);
    
    record GoogleUserInfo(String email, String name, String avatarUrl) {}
}
