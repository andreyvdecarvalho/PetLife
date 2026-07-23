package com.petlife.modules.pet.infrastructure.persistence;

import com.petlife.modules.pet.infrastructure.persistence.entity.WeightRecordJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

/**
 * Repository for WeightRecord entities.
 */
@Repository
public interface WeightRecordJpaRepository extends JpaRepository<WeightRecordJpaEntity, UUID> {
    List<WeightRecordJpaEntity> findByPetIdOrderByRecordedAtDesc(UUID petId);
}
