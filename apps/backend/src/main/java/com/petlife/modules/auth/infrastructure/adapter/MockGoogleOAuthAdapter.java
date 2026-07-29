package com.petlife.modules.auth.infrastructure.adapter;

import com.petlife.modules.auth.application.port.OAuthProviderPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("e2e")
public class MockGoogleOAuthAdapter implements OAuthProviderPort {
    @Override
    public GoogleUserInfo getGoogleUserInfo(String idToken) {
        return new GoogleUserInfo("mock-google@petlife.com", "Mock Google User", "http://avatar.com/mock");
    }
}
