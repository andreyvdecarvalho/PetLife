package com.petlife.modules.medication.domain.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Medication {

    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UUID petId;

    private String name;
    private String dosage;
    private MedicationFrequency frequency;
    private MedicationType medicationType = MedicationType.MEDICINE;
    private Integer customFrequencyHours;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> timesOfDay = new ArrayList<>();
    private MedicationStatus status = MedicationStatus.ACTIVE;
    private List<MedicationAdministration> administrations = new ArrayList<>();

    /**
     * ID do usuário dono do pet. Preenchido pelo adapter ao carregar a entidade
     * quando o contexto de autorização precisar verificar a propriedade.
     */
    private UUID petOwnerId;
}
