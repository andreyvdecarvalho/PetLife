package com.petlife.modules.medication.infrastructure.persistence;

import com.petlife.modules.medication.application.port.MedicationRepositoryPort;
import com.petlife.modules.medication.domain.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MedicationJpaRepository extends JpaRepository<Medication, UUID>, MedicationRepositoryPort {
}
