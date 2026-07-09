package com.petlife.modules.notification.infrastructure.service;

import com.petlife.modules.notification.application.port.FcmServicePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MockFcmService implements FcmServicePort {
    @Override
    public void sendPushNotification(String token, String title, String body) {
        log.info("Sending FCM push notification to token {}: [{}] {}", token, title, body);
    }
}
