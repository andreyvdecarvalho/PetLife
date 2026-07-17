package com.petlife.modules.pet.infrastructure.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record UpdateWeightRecordRequest(
    @NotNull(message = "O peso é obrigatório")
    BigDecimal weightKg,

    @NotNull(message = "A data de registro é obrigatória")
    OffsetDateTime recordedAt
) {}
