package com.petlife.modules.veterinarian.application.port;

import com.petlife.modules.veterinarian.domain.entity.VetSchedule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VetScheduleRepositoryPort {
    VetSchedule save(VetSchedule schedule);
    Optional<VetSchedule> findByIdAndVeterinarianId(UUID id, UUID veterinarianId);
    List<VetSchedule> findByVeterinarianId(UUID veterinarianId);
    void delete(VetSchedule schedule);
}
