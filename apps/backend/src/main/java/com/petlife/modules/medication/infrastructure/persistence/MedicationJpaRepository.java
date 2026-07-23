package com.petlife.modules.medication.infrastructure.persistence;

import com.petlife.modules.medication.domain.entity.MedicationStatus;
import com.petlife.modules.medication.infrastructure.persistence.entity.MedicationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MedicationJpaRepository extends JpaRepository<MedicationJpaEntity, UUID> {
    List<MedicationJpaEntity> findByPetEntityId(UUID petId);
    List<MedicationJpaEntity> findByPetEntityIdAndStatus(UUID petId, MedicationStatus status);
}
