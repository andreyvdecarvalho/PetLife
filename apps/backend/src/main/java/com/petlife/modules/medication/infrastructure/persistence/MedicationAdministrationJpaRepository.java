package com.petlife.modules.medication.infrastructure.persistence;

import com.petlife.modules.medication.application.port.MedicationAdministrationRepositoryPort;
import com.petlife.modules.medication.domain.entity.MedicationAdministration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MedicationAdministrationJpaRepository extends JpaRepository<MedicationAdministration, UUID>, MedicationAdministrationRepositoryPort {
}
