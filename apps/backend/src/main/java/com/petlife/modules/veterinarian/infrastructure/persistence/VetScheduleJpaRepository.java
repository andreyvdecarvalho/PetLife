package com.petlife.modules.veterinarian.infrastructure.persistence;

import com.petlife.modules.veterinarian.infrastructure.persistence.entity.VetScheduleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VetScheduleJpaRepository extends JpaRepository<VetScheduleJpaEntity, UUID> {
    List<VetScheduleJpaEntity> findByVeterinarianId(UUID veterinarianId);
    Optional<VetScheduleJpaEntity> findByIdAndVeterinarianId(UUID id, UUID veterinarianId);
}
