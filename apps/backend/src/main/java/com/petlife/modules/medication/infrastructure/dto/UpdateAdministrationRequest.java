package com.petlife.modules.medication.infrastructure.dto;

import com.petlife.modules.medication.domain.entity.MedicationAdministrationStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateAdministrationRequest(
    @NotNull
    MedicationAdministrationStatus status,

    String skippedReason
) {}
