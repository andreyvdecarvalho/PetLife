package com.petlife.modules.pet.infrastructure.dto;

import com.petlife.modules.pet.domain.entity.RoutineActivityStatus;
import com.petlife.modules.pet.domain.entity.RoutineActivityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateRoutineActivityRequest(
        @NotBlank(message = "Title is required")
        String title,
        
        String description,
        
        @NotNull(message = "Activity date is required")
        LocalDate activityDate,
        
        LocalTime activityTime,
        
        @NotNull(message = "Type is required")
        RoutineActivityType type,
        
        @NotNull(message = "Status is required")
        RoutineActivityStatus status
) {}
