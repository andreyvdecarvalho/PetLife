package com.petlife.modules.pet.infrastructure.dto;

import com.petlife.modules.pet.entity.RoutineActivity;
import com.petlife.modules.pet.entity.RoutineActivityStatus;
import com.petlife.modules.pet.entity.RoutineActivityType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record RoutineActivityResponse(
        UUID id,
        UUID petId,
        String title,
        String description,
        LocalDate activityDate,
        LocalTime activityTime,
        RoutineActivityType type,
        RoutineActivityStatus status,
        LocalDateTime createdAt
) {
    public static RoutineActivityResponse fromEntity(RoutineActivity activity) {
        return new RoutineActivityResponse(
                activity.getId(),
                activity.getPet().getId(),
                activity.getTitle(),
                activity.getDescription(),
                activity.getActivityDate(),
                activity.getActivityTime(),
                activity.getType(),
                activity.getStatus(),
                activity.getCreatedAt()
        );
    }
}
