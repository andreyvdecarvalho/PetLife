package com.petlife.modules.pet.infrastructure.persistence;

import com.petlife.modules.pet.infrastructure.persistence.entity.RoutineActivityJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface RoutineActivityJpaRepository extends JpaRepository<RoutineActivityJpaEntity, UUID> {
    List<RoutineActivityJpaEntity> findByPetIdAndActivityDateOrderByActivityTimeAsc(UUID petId, LocalDate activityDate);
    List<RoutineActivityJpaEntity> findByPetIdOrderByActivityDateAscActivityTimeAsc(UUID petId);
}
