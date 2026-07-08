package com.petlife.modules.pet.infrastructure.dto;

import com.petlife.modules.pet.entity.GroomingType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class GroomingResponse {
    private UUID id;
    private UUID petId;
    private GroomingType type;
    private LocalDate date;
    private String provider;
    private BigDecimal cost;
    private Integer frequencyDays;
    private LocalDate nextDate;
    private String notes;
    private List<String> photos;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
