package com.petlife.modules.medication.infrastructure.dto;

import com.petlife.modules.medication.domain.entity.MedicationFrequency;
import com.petlife.modules.medication.domain.entity.MedicationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record MedicationResponse(
    UUID id,
    UUID petId,
    String name,
    String dosage,
    MedicationFrequency frequency,
    Integer customFrequencyHours,
    LocalDate startDate,
    LocalDate endDate,
    List<String> timesOfDay,
    MedicationStatus status,
    List<MedicationAdministrationResponse> administrations,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
