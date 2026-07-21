package com.petlife.modules.medication.application.port;

import com.petlife.modules.medication.domain.entity.MedicationAdministration;
import com.petlife.modules.medication.domain.entity.MedicationAdministrationStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicationAdministrationRepositoryPort {
    MedicationAdministration save(MedicationAdministration administration);
    <S extends MedicationAdministration> List<S> saveAll(Iterable<S> administrations);
    Optional<MedicationAdministration> findById(UUID id);
    void delete(MedicationAdministration administration);
    void deleteAll(Iterable<? extends MedicationAdministration> administrations);
    List<MedicationAdministration> findByMedicationIdAndStatusAndScheduledTimeAfter(
            UUID medicationId, MedicationAdministrationStatus status, OffsetDateTime time);
    List<MedicationAdministration> findByMedicationPetEntityId(UUID petId);
}
