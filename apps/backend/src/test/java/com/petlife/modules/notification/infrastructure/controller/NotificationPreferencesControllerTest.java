package com.petlife.modules.notification.infrastructure.controller;

import com.petlife.modules.auth.entity.User;
import com.petlife.modules.auth.repository.UserRepository;
import com.petlife.modules.notification.infrastructure.dto.DeviceTokenRequest;
import com.petlife.modules.notification.infrastructure.dto.NotificationPreferencesRequest;
import com.petlife.shared.IntegrationTestBase;
import com.petlife.shared.factories.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import java.time.LocalTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("NotificationPreferencesController Integration Tests")
class NotificationPreferencesControllerTest extends IntegrationTestBase {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        user = UserFactory.make();
        userRepository.save(user);
    }

    @Test
    @DisplayName("GET /api/v1/users/me/notification-preferences - Deve retornar preferencias de notificacao")
    void shouldGetPreferences() throws Exception {
        mockMvc.perform(get("/api/v1/users/me/notification-preferences")
                        .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(user.getId().toString()))
                .andExpect(jsonPath("$.data.pushEnabled").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/users/me/notification-preferences - Deve atualizar preferencias de notificacao")
    void shouldUpdatePreferences() throws Exception {
        NotificationPreferencesRequest request = new NotificationPreferencesRequest(
                false, false, false, false, false, false, false,
                LocalTime.of(23, 0), LocalTime.of(6, 0)
        );

        mockMvc.perform(put("/api/v1/users/me/notification-preferences")
                        .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.pushEnabled").value(false))
                .andExpect(jsonPath("$.data.emailEnabled").value(false))
                .andExpect(jsonPath("$.data.doNotDisturbStart").value("23:00:00"));
    }

    @Test
    @DisplayName("POST /api/v1/users/me/device-tokens - Deve registrar token FCM")
    void shouldRegisterDeviceToken() throws Exception {
        DeviceTokenRequest request = new DeviceTokenRequest("valid-fcm-token");

        mockMvc.perform(post("/api/v1/users/me/device-tokens")
                        .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getFcmToken()).isEqualTo("valid-fcm-token");
    }
}
