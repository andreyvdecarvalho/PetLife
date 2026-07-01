package com.petlife.modules.pet.infrastructure.persistence;

import com.petlife.modules.pet.entity.WeightRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link WeightRecord} entities.
 * Provides query to fetch weight history ordered by recording date descending.
 */
@Repository
public interface WeightRecordJpaRepository extends JpaRepository<WeightRecord, UUID> {
    List<WeightRecord> findByPetIdOrderByRecordedAtDesc(UUID petId);
}
