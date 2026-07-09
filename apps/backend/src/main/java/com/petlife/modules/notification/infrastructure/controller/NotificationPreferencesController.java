package com.petlife.modules.notification.infrastructure.controller;

import com.petlife.modules.notification.application.usecase.GetNotificationPreferencesUseCase;
import com.petlife.modules.notification.application.usecase.RegisterDeviceTokenUseCase;
import com.petlife.modules.notification.application.usecase.UpdateNotificationPreferencesUseCase;
import com.petlife.modules.notification.infrastructure.dto.DeviceTokenRequest;
import com.petlife.modules.notification.infrastructure.dto.NotificationPreferencesRequest;
import com.petlife.modules.notification.infrastructure.dto.NotificationPreferencesResponse;
import com.petlife.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
@Tag(name = "Preferências de Notificação", description = "Endpoints de preferências de notificação do usuário")
public class NotificationPreferencesController {

    private final GetNotificationPreferencesUseCase getPreferencesUseCase;
    private final UpdateNotificationPreferencesUseCase updatePreferencesUseCase;
    private final RegisterDeviceTokenUseCase registerDeviceTokenUseCase;

    @GetMapping("/notification-preferences")
    @Operation(summary = "Obter preferências de notificação do tutor")
    public ApiResponse<NotificationPreferencesResponse> getPreferences(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return ApiResponse.of(getPreferencesUseCase.execute(userId));
    }

    @PutMapping("/notification-preferences")
    @Operation(summary = "Atualizar preferências de notificação do tutor")
    public ApiResponse<NotificationPreferencesResponse> updatePreferences(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody NotificationPreferencesRequest request) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return ApiResponse.of(updatePreferencesUseCase.execute(userId, request));
    }

    @PostMapping("/device-tokens")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Registrar token do dispositivo para push notifications")
    public void registerDeviceToken(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody DeviceTokenRequest request) {
        UUID userId = UUID.fromString(jwt.getSubject());
        registerDeviceTokenUseCase.execute(userId, request.fcmToken());
    }
}
