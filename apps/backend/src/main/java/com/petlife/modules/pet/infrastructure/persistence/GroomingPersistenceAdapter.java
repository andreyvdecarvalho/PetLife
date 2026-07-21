package com.petlife.modules.pet.infrastructure.persistence;

import com.petlife.modules.pet.application.port.GroomingRepositoryPort;
import com.petlife.modules.pet.domain.entity.Grooming;
import com.petlife.modules.pet.infrastructure.persistence.mapper.GroomingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GroomingPersistenceAdapter implements GroomingRepositoryPort {

    private final JpaGroomingRepository repository;
    private final GroomingMapper mapper;

    @Override
    public Grooming save(Grooming grooming) {
        return mapper.toDomain(repository.save(mapper.toJpaEntity(grooming)));
    }

    @Override
    public Optional<Grooming> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Grooming> findAllByPetId(UUID petId) {
        return repository.findByPetIdOrderByDateDesc(petId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Grooming grooming) {
        repository.delete(mapper.toJpaEntity(grooming));
    }

    @Override
    public List<Grooming> findByNextDate(java.time.LocalDate nextDate) {
        return repository.findByNextDate(nextDate).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
