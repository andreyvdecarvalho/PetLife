package com.petlife.modules.notification.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;

public record DeviceTokenRequest(
        @NotBlank(message = "fcmToken é obrigatório")
        String fcmToken
) {}
