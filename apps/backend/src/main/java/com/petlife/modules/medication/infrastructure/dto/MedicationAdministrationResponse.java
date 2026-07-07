package com.petlife.modules.medication.infrastructure.dto;

import com.petlife.modules.medication.domain.entity.MedicationAdministrationStatus;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public record MedicationAdministrationResponse(
    UUID id,
    UUID medicationId,
    String medicationName,
    OffsetDateTime scheduledTime,
    OffsetDateTime administeredAt,
    MedicationAdministrationStatus status,
    String skippedReason,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
