package com.petlife.modules.notification.infrastructure.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.petlife.modules.notification.application.port.FcmServicePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@Slf4j
public class FcmService implements FcmServicePort {

    @Override
    public void sendPushNotification(String token, String title, String body) {
        if (token == null || token.isBlank()) {
            return;
        }

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent message to FCM: {}. Response: {}", token, response);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send FCM message to token: {}", token, e);
        } catch (Exception e) {
            log.warn("Firebase App is probably not initialized. Fallback to mock behavior for token {}", token);
            log.info("Sending FCM push notification to token {}: [{}] {}", token, title, body);
        }
    }
}
