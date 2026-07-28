package com.petlife.modules.medication.infrastructure.dto;

import com.petlife.modules.medication.domain.entity.MedicationFrequency;
import com.petlife.modules.medication.domain.entity.MedicationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record CreateMedicationRequest(
    @NotBlank @Size(min = 2, max = 200)
    String name,

    @NotBlank @Size(min = 1, max = 100)
    String dosage,

    @NotNull
    MedicationFrequency frequency,

    MedicationType medicationType,

    Integer customFrequencyHours,

    @NotNull
    LocalDate startDate,

    LocalDate endDate,

    @NotEmpty
    List<String> timesOfDay,

    String prescribedBy,

    String reason
) {
    public CreateMedicationRequest(
        String name,
        String dosage,
        MedicationFrequency frequency,
        MedicationType medicationType,
        Integer customFrequencyHours,
        LocalDate startDate,
        LocalDate endDate,
        List<String> timesOfDay
    ) {
        this(name, dosage, frequency, medicationType, customFrequencyHours, startDate, endDate, timesOfDay, null, null);
    }
}
