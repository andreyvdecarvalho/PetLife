package com.petlife.modules.medication.application.port;

import com.petlife.modules.medication.domain.entity.Medication;
import com.petlife.modules.medication.domain.entity.MedicationStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicationRepositoryPort {
    Medication save(Medication medication);
    Optional<Medication> findById(UUID id);
    List<Medication> findByPetId(UUID petId);
    List<Medication> findByPetIdAndStatus(UUID petId, MedicationStatus status);
}
