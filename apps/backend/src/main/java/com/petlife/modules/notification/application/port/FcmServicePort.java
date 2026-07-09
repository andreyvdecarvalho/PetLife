package com.petlife.modules.notification.application.port;

public interface FcmServicePort {
    void sendPushNotification(String token, String title, String body);
}
