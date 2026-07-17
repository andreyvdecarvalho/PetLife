package com.petlife.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${firebase.service-account-json:}")
    private String serviceAccountJson;

    @PostConstruct
    public void initFirebase() {
        if (serviceAccountJson == null || serviceAccountJson.isBlank()) {
            log.warn("firebase.service-account-json property is missing or blank. "
                    + "Firebase Admin SDK will NOT be initialized.");
            return;
        }

        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream credentialsStream = new ByteArrayInputStream(
                        serviceAccountJson.getBytes(StandardCharsets.UTF_8));
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                        .build();

                FirebaseApp.initializeApp(options);
                log.info("Firebase Admin SDK initialized successfully.");
            }
        } catch (IOException e) {
            log.error("Failed to initialize Firebase Admin SDK", e);
        }
    }
}
