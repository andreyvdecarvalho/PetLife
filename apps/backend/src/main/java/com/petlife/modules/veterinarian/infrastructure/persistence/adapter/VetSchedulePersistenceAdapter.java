package com.petlife.modules.veterinarian.infrastructure.persistence.adapter;

import com.petlife.modules.veterinarian.application.port.VetScheduleRepositoryPort;
import com.petlife.modules.veterinarian.entity.VetSchedule;
import com.petlife.modules.veterinarian.infrastructure.persistence.VetScheduleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VetSchedulePersistenceAdapter implements VetScheduleRepositoryPort {

    private final VetScheduleJpaRepository repository;

    @Override
    public VetSchedule save(VetSchedule schedule) {
        return repository.save(schedule);
    }

    @Override
    public Optional<VetSchedule> findByIdAndVeterinarianId(UUID id, UUID veterinarianId) {
        return repository.findByIdAndVeterinarianId(id, veterinarianId);
    }

    @Override
    public List<VetSchedule> findByVeterinarianId(UUID veterinarianId) {
        return repository.findByVeterinarianId(veterinarianId);
    }

    @Override
    public void delete(VetSchedule schedule) {
        repository.delete(schedule);
    }
}
