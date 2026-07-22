package com.petlife.modules.veterinarian.domain.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.UUID;
import java.time.LocalDateTime;

@Getter
@Setter
public class VetSchedule {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private Veterinarian veterinarian;
    private java.time.DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isAvailable = true;
}
