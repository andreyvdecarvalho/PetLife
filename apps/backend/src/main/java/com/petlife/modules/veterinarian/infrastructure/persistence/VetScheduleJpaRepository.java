package com.petlife.modules.veterinarian.infrastructure.persistence;

import com.petlife.modules.veterinarian.entity.VetSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VetScheduleJpaRepository extends JpaRepository<VetSchedule, UUID> {
    List<VetSchedule> findByVeterinarianId(UUID veterinarianId);
    Optional<VetSchedule> findByIdAndVeterinarianId(UUID id, UUID veterinarianId);
}
