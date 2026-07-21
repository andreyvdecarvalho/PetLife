package com.petlife.modules.pet.application.port;

import com.petlife.modules.pet.domain.entity.RoutineActivity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoutineActivityRepositoryPort {
    RoutineActivity save(RoutineActivity routineActivity);
    Optional<RoutineActivity> findById(UUID id);
    List<RoutineActivity> findByPetIdAndActivityDateOrderByActivityTimeAsc(UUID petId, LocalDate activityDate);
    List<RoutineActivity> findByPetIdOrderByActivityDateAscActivityTimeAsc(UUID petId);
}
