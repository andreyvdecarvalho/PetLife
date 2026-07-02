package com.petlife.modules.pet.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
public class CreateConsultationRequest {

    @NotNull(message = "Date is required")
    private OffsetDateTime date;

    @Size(max = 200, message = "Veterinarian must be less than 200 characters")
    private String veterinarian;

    @Size(max = 200, message = "Clinic must be less than 200 characters")
    private String clinic;

    @NotBlank(message = "Reason is required")
    @Size(min = 3, max = 500, message = "Reason must be between 3 and 500 characters")
    private String reason;

    private String diagnosis;

    private String prescriptions;

    private String notes;

    private BigDecimal weightAtVisit;

    private LocalDate followUpDate;

    private BigDecimal cost;
}
