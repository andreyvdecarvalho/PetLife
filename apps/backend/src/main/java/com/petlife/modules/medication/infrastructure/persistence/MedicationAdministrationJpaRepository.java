package com.petlife.modules.medication.infrastructure.persistence;

import com.petlife.modules.medication.domain.entity.MedicationAdministrationStatus;
import com.petlife.modules.medication.infrastructure.persistence.entity.MedicationAdministrationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MedicationAdministrationJpaRepository extends JpaRepository<MedicationAdministrationJpaEntity, UUID> {

    List<MedicationAdministrationJpaEntity> findByStatusAndScheduledTimeBefore(
            MedicationAdministrationStatus status,
            OffsetDateTime time
    );

    List<MedicationAdministrationJpaEntity> findByMedicationIdAndStatusAndScheduledTimeAfter(
            UUID medicationId, MedicationAdministrationStatus status, OffsetDateTime time);

    List<MedicationAdministrationJpaEntity> findByMedicationPetId(UUID petId);
}
