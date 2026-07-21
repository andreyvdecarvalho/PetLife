package com.petlife.modules.pet.infrastructure.dto;

import com.petlife.modules.pet.domain.entity.PetStatus;
import jakarta.validation.constraints.NotNull;

public record UpdatePetStatusRequest(
    @NotNull(message = "O status é obrigatório")
    PetStatus status
) {}
