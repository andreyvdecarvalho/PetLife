package com.petlife.modules.medication.domain.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
public class MedicationAdministration {

    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** Referência de domínio (preenchida quando carregada junto com a Medication). */
    private Medication medication;

    /** Campos planos para evitar navegação de grafo JPA nos Use Cases. */
    private UUID medicationId;
    private String medicationName;
    private UUID petOwnerId;

    private OffsetDateTime scheduledTime;
    private OffsetDateTime administeredAt;
    private MedicationAdministrationStatus status = MedicationAdministrationStatus.PENDING;
    private String skippedReason;
}
