package com.petlife.modules.pet.infrastructure.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class UpdateConsultationRequest {
    private String reason;
    private String diagnosis;
    private String prescriptions;
    private String notes;
    private BigDecimal weightAtVisit;
    private LocalDate followUpDate;
}
