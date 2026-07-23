package com.petlife.modules.notification.infrastructure.controller;

import com.petlife.modules.notification.application.usecase.GetNotificationsUseCase;
import com.petlife.modules.notification.application.usecase.MarkAllNotificationsReadUseCase;
import com.petlife.modules.notification.application.usecase.MarkNotificationReadUseCase;
import com.petlife.modules.notification.infrastructure.dto.NotificationResponse;
import com.petlife.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificações", description = "Endpoints de gerenciamento e leitura de notificações in-app")
public class NotificationController {

    private final GetNotificationsUseCase getNotificationsUseCase;
    private final MarkNotificationReadUseCase markNotificationReadUseCase;
    private final MarkAllNotificationsReadUseCase markAllNotificationsReadUseCase;

    @GetMapping
    @Operation(summary = "Obter lista de notificações do tutor (paginada)")
    public ApiResponse<List<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        UUID userId = UUID.fromString(jwt.getSubject());
        var result = getNotificationsUseCase.execute(userId, page, size);
        return ApiResponse.paged(result.content(), result.meta());
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Marcar uma notificação como lida")
    public ApiResponse<NotificationResponse> readNotification(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return ApiResponse.of(markNotificationReadUseCase.execute(userId, id));
    }

    @PatchMapping("/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Marcar todas as notificações do tutor como lidas")
    public void readAllNotifications(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        markAllNotificationsReadUseCase.execute(userId);
    }
}
