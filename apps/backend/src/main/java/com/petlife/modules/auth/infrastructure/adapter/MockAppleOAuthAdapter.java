package com.petlife.modules.auth.infrastructure.adapter;

import com.petlife.modules.auth.application.port.AppleOAuthPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class MockAppleOAuthAdapter implements AppleOAuthPort {
    @Override
    public AppleUserInfo getAppleUserInfo(String idToken) {
        return new AppleUserInfo("mock-apple@petlife.com");
    }
}
