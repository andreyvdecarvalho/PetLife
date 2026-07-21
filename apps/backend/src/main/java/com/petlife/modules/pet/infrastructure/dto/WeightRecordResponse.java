package com.petlife.modules.pet.infrastructure.dto;

import com.petlife.modules.pet.domain.entity.WeightRecord;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for exposing weight records via API.
 */
public record WeightRecordResponse(
        UUID id,
        BigDecimal weightKg,
        OffsetDateTime recordedAt
) {
    public static WeightRecordResponse fromEntity(WeightRecord entity) {
        return new WeightRecordResponse(
                entity.getId(),
                entity.getWeightKg(),
                entity.getRecordedAt()
        );
    }
}
