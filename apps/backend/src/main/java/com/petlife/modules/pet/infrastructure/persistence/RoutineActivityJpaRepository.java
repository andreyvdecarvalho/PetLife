package com.petlife.modules.pet.infrastructure.persistence;

import com.petlife.modules.pet.application.port.RoutineActivityRepositoryPort;
import com.petlife.modules.pet.entity.RoutineActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoutineActivityJpaRepository extends JpaRepository<RoutineActivity, UUID>, RoutineActivityRepositoryPort {
}
