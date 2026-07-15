package com.petlife.modules.notification.infrastructure.controller;

import com.petlife.modules.auth.entity.User;
import com.petlife.modules.auth.repository.UserRepository;
import com.petlife.modules.notification.domain.entity.NotificationMessage;
import com.petlife.modules.notification.domain.entity.NotificationType;
import com.petlife.modules.notification.infrastructure.persistence.NotificationMessageJpaRepository;
import com.petlife.shared.IntegrationTestBase;
import com.petlife.shared.factories.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("NotificationController Integration Tests")
class NotificationControllerTest extends IntegrationTestBase {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationMessageJpaRepository messageJpaRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserFactory.make();
        userRepository.save(user);
    }

    @Test
    @DisplayName("GET /api/v1/notifications - Deve retornar lista paginada de notificacoes")
    void shouldGetPagedNotifications() throws Exception {
        NotificationMessage msg = new NotificationMessage();
        msg.setUserId(user.getId());
        msg.setType(NotificationType.SYSTEM);
        msg.setTitle("System Alert");
        msg.setBody("Your account is verified");
        msg.setRead(false);
        messageJpaRepository.save(msg);

        mockMvc.perform(get("/api/v1/notifications")
                        .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].title").value("System Alert"))
                .andExpect(jsonPath("$.data[0].read").value(false));
    }

    @Test
    @DisplayName("PATCH /api/v1/notifications/{id}/read - Deve marcar notificacao como lida")
    void shouldMarkNotificationAsRead() throws Exception {
        NotificationMessage msg = new NotificationMessage();
        msg.setUserId(user.getId());
        msg.setType(NotificationType.SYSTEM);
        msg.setTitle("System Alert");
        msg.setBody("Your account is verified");
        msg.setRead(false);
        messageJpaRepository.save(msg);

        mockMvc.perform(patch("/api/v1/notifications/{id}/read", msg.getId())
                        .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.read").value(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/notifications/read-all - Deve marcar todas como lidas")
    void shouldMarkAllNotificationsAsRead() throws Exception {
        NotificationMessage msg1 = new NotificationMessage();
        msg1.setUserId(user.getId());
        msg1.setType(NotificationType.SYSTEM);
        msg1.setTitle("Alert 1");
        msg1.setBody("Body 1");
        msg1.setRead(false);
        messageJpaRepository.save(msg1);

        mockMvc.perform(patch("/api/v1/notifications/read-all")
                        .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail()))))
                .andExpect(status().isNoContent());
    }
}
