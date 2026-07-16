package com.petlife.modules.medication.infrastructure.dto;

import com.petlife.modules.medication.domain.entity.MedicationFrequency;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateMedicationRequest {
    private String name;
    private String dosage;
    private MedicationFrequency frequency;
    private Integer customFrequencyHours;
    private LocalDate endDate;
    private List<String> timesOfDay;
}
