package com.petlife.modules.pet.infrastructure.adapter;

import com.petlife.modules.pet.application.port.RoutineActivityRepositoryPort;
import com.petlife.modules.pet.domain.entity.RoutineActivity;
import com.petlife.modules.pet.infrastructure.persistence.RoutineActivityJpaRepository;
import com.petlife.modules.pet.infrastructure.persistence.mapper.RoutineActivityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoutineActivityAdapter implements RoutineActivityRepositoryPort {

    private final RoutineActivityJpaRepository repository;
    private final RoutineActivityMapper mapper;

    @Override
    public RoutineActivity save(RoutineActivity routineActivity) {
        return mapper.toDomain(repository.save(mapper.toJpaEntity(routineActivity)));
    }

    @Override
    public Optional<RoutineActivity> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<RoutineActivity> findByPetIdAndActivityDateOrderByActivityTimeAsc(UUID petId, LocalDate activityDate) {
        return repository.findByPetIdAndActivityDateOrderByActivityTimeAsc(petId, activityDate).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoutineActivity> findByPetIdOrderByActivityDateAscActivityTimeAsc(UUID petId) {
        return repository.findByPetIdOrderByActivityDateAscActivityTimeAsc(petId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
