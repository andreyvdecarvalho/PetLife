package com.petlife.modules.veterinarian.infrastructure.dto.request;

import com.petlife.modules.veterinarian.domain.entity.Modality;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SearchVeterinariansRequest(
    BigDecimal latitude,
    BigDecimal longitude,
    Double radiusKm,
    Modality modality,
    String specialty,
    Boolean emergencyOnDuty,
    int page,
    int size
) {}
