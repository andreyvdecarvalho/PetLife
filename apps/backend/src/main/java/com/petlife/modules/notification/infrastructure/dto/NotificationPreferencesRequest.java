package com.petlife.modules.notification.infrastructure.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record NotificationPreferencesRequest(
        @NotNull(message = "pushEnabled é obrigatório")
        Boolean pushEnabled,

        @NotNull(message = "emailEnabled é obrigatório")
        Boolean emailEnabled,

        @NotNull(message = "vaccines é obrigatório")
        Boolean vaccines,

        @NotNull(message = "medications é obrigatório")
        Boolean medications,

        @NotNull(message = "appointments é obrigatório")
        Boolean appointments,

        @NotNull(message = "grooming é obrigatório")
        Boolean grooming,

        @NotNull(message = "marketing é obrigatório")
        Boolean marketing,

        @NotNull(message = "doNotDisturbStart é obrigatório")
        LocalTime doNotDisturbStart,

        @NotNull(message = "doNotDisturbEnd é obrigatório")
        LocalTime doNotDisturbEnd
) {}
