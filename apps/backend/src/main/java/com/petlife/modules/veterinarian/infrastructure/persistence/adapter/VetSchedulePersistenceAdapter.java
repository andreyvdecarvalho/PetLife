package com.petlife.modules.veterinarian.infrastructure.persistence.adapter;

import com.petlife.modules.veterinarian.application.port.VetScheduleRepositoryPort;
import com.petlife.modules.veterinarian.domain.entity.VetSchedule;
import com.petlife.modules.veterinarian.infrastructure.persistence.VetScheduleJpaRepository;
import com.petlife.modules.veterinarian.infrastructure.persistence.mapper.VeterinarianMapper;
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
        return VeterinarianMapper.toDomain(
            repository.save(VeterinarianMapper.toJpaEntity(schedule))
        );
    }

    @Override
    public Optional<VetSchedule> findByIdAndVeterinarianId(UUID id, UUID veterinarianId) {
        return repository.findByIdAndVeterinarianId(id, veterinarianId)
            .map(VeterinarianMapper::toDomain);
    }

    @Override
    public List<VetSchedule> findByVeterinarianId(UUID veterinarianId) {
        return repository.findByVeterinarianId(veterinarianId).stream()
            .map(VeterinarianMapper::toDomain)
            .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void delete(VetSchedule schedule) {
        repository.delete(VeterinarianMapper.toJpaEntity(schedule));
    }
}
