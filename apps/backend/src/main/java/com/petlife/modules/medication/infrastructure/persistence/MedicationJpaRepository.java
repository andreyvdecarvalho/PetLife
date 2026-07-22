package com.petlife.modules.medication.infrastructure.persistence;

import com.petlife.modules.medication.domain.entity.MedicationStatus;
import com.petlife.modules.medication.infrastructure.persistence.entity.MedicationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MedicationJpaRepository extends JpaRepository<MedicationJpaEntity, UUID> {
    List<MedicationJpaEntity> findByPetId(UUID petId);
    List<MedicationJpaEntity> findByPetIdAndStatus(UUID petId, MedicationStatus status);
}
