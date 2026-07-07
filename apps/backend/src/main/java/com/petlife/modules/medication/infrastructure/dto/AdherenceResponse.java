package com.petlife.modules.medication.infrastructure.dto;

public record AdherenceResponse(
    double adherenceRate,
    long totalDoses,
    long takenDoses,
    long skippedDoses,
    long lateDoses,
    long pendingDoses
) {}
