package com.petlife.modules.veterinarian.infrastructure.dto.request;

import com.petlife.modules.veterinarian.domain.entity.AvailabilityStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateAvailabilityRequest(
    @NotNull(message = "O status de disponibilidade é obrigatório.")
    AvailabilityStatus availabilityStatus,
    
    @NotNull(message = "O status de plantão é obrigatório.")
    Boolean emergencyOnDuty
) {}
