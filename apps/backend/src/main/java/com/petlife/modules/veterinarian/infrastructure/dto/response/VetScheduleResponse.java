package com.petlife.modules.veterinarian.infrastructure.dto.response;

import com.petlife.modules.veterinarian.entity.VetSchedule;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record VetScheduleResponse(
    UUID id,
    DayOfWeek dayOfWeek,
    LocalTime openTime,
    LocalTime closeTime,
    boolean isActive
) {
    public static VetScheduleResponse fromEntity(VetSchedule entity) {
        return new VetScheduleResponse(
            entity.getId(),
            entity.getDayOfWeek(),
            entity.getOpenTime(),
            entity.getCloseTime(),
            entity.isActive()
        );
    }
}
