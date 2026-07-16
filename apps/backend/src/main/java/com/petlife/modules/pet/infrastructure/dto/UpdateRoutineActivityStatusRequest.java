package com.petlife.modules.pet.infrastructure.dto;

import com.petlife.modules.pet.entity.RoutineActivityStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateRoutineActivityStatusRequest(
        @NotNull(message = "Status is required")
        RoutineActivityStatus status
) {}
