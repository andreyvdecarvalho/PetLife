package com.petlife.modules.pet.infrastructure.persistence;

import com.petlife.modules.pet.application.port.GroomingRepositoryPort;
import com.petlife.modules.pet.entity.Grooming;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GroomingPersistenceAdapter implements GroomingRepositoryPort {

    private final JpaGroomingRepository repository;

    @Override
    public Grooming save(Grooming grooming) {
        return repository.save(grooming);
    }

    @Override
    public Optional<Grooming> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Grooming> findAllByPetId(UUID petId) {
        return repository.findByPetIdOrderByDateDesc(petId);
    }

    @Override
    public void delete(Grooming grooming) {
        repository.delete(grooming);
    }
}
