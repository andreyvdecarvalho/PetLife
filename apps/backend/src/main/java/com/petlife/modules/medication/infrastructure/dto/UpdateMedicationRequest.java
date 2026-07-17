package com.petlife.modules.medication.infrastructure.dto;

import com.petlife.modules.medication.domain.entity.MedicationFrequency;
import com.petlife.modules.medication.domain.entity.MedicationType;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMedicationRequest {
    private String name;
    private String dosage;
    private MedicationFrequency frequency;
    private MedicationType medicationType;
    private Integer customFrequencyHours;
    private LocalDate endDate;
    private List<String> timesOfDay;
}
