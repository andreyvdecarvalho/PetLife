package com.petlife.modules.pet.domain.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Consultation {

    private UUID id;
    private Pet pet;
    private OffsetDateTime date;
    private String veterinarian;
    private String clinic;
    private String reason;
    private String diagnosis;
    private String prescriptions;
    private String notes;
    private BigDecimal weightAtVisit;
    private LocalDate followUpDate;
    private BigDecimal cost;
    private List<String> attachments = new ArrayList<>();
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
