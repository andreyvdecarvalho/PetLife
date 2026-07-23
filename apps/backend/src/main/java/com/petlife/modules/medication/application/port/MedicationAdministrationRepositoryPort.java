package com.petlife.modules.medication.application.port;

import com.petlife.modules.medication.domain.entity.MedicationAdministration;
import com.petlife.modules.medication.domain.entity.MedicationAdministrationStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicationAdministrationRepositoryPort {
    MedicationAdministration save(MedicationAdministration administration);
    List<MedicationAdministration> saveAll(List<MedicationAdministration> administrations);
    Optional<MedicationAdministration> findById(UUID id);
    void delete(MedicationAdministration administration);
    void deleteAll(List<MedicationAdministration> administrations);
    List<MedicationAdministration> findByMedicationIdAndStatusAndScheduledTimeAfter(
            UUID medicationId, MedicationAdministrationStatus status, OffsetDateTime time);
    List<MedicationAdministration> findByMedicationPetId(UUID petId);

    /**
     * Retorna administrações com o status informado cujo horário agendado
     * seja anterior ao instante fornecido. Usado pelo NotificationScheduler
     * para detectar doses atrasadas sem acessar o JpaRepository diretamente.
     */
    List<MedicationAdministration> findByStatusAndScheduledTimeBefore(
            MedicationAdministrationStatus status, OffsetDateTime time);
}
