package com.petlife.modules.veterinarian.infrastructure.persistence.adapter;

import com.petlife.modules.veterinarian.application.port.VeterinarianRepositoryPort;
import com.petlife.modules.veterinarian.entity.Veterinarian;
import com.petlife.modules.veterinarian.infrastructure.persistence.VeterinarianJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VeterinarianPersistenceAdapter implements VeterinarianRepositoryPort {

    private final VeterinarianJpaRepository repository;

    @Override
    public Veterinarian save(Veterinarian veterinarian) {
        return repository.save(veterinarian);
    }

    @Override
    public Optional<Veterinarian> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Veterinarian> findByUserId(UUID userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public boolean existsByCrmvNumber(String crmvNumber) {
        return repository.existsByCrmvNumber(crmvNumber);
    }
}
