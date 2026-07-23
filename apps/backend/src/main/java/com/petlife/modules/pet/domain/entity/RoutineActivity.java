package com.petlife.modules.pet.domain.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
public class RoutineActivity {

    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Pet pet;
    private String title;
    private String description;
    private LocalDate activityDate;
    private LocalTime activityTime;
    private RoutineActivityType type;
    private RoutineActivityStatus status;
}
