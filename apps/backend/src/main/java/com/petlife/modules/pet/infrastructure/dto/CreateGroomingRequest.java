package com.petlife.modules.pet.infrastructure.dto;

import com.petlife.modules.pet.domain.entity.GroomingType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreateGroomingRequest {

    @NotNull(message = "Grooming type is required")
    private GroomingType type;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @Size(max = 200, message = "Provider must be less than 200 characters")
    private String provider;

    private BigDecimal cost;

    private Integer frequencyDays;

    private String notes;
}
