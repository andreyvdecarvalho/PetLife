package com.petlife.modules.medication.infrastructure.persistence;

import com.petlife.modules.medication.application.port.MedicationAdministrationRepositoryPort;
import com.petlife.modules.medication.domain.entity.MedicationAdministration;
import com.petlife.modules.medication.domain.entity.MedicationAdministrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MedicationAdministrationJpaRepository
        extends JpaRepository<MedicationAdministration, UUID>, MedicationAdministrationRepositoryPort {

    List<MedicationAdministration> findByStatusAndScheduledTimeBefore(
            MedicationAdministrationStatus status,
            OffsetDateTime time
    );
}
