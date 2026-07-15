package com.petlife.modules.veterinarian.infrastructure.persistence.adapter;

import com.petlife.modules.veterinarian.application.port.VetAddressRepositoryPort;
import com.petlife.modules.veterinarian.entity.VetAddress;
import com.petlife.modules.veterinarian.infrastructure.persistence.VetAddressJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VetAddressPersistenceAdapter implements VetAddressRepositoryPort {

    private final VetAddressJpaRepository repository;

    @Override
    public VetAddress save(VetAddress address) {
        return repository.save(address);
    }

    @Override
    public Optional<VetAddress> findByIdAndVeterinarianId(UUID id, UUID veterinarianId) {
        return repository.findByIdAndVeterinarianId(id, veterinarianId);
    }

    @Override
    public List<VetAddress> findByVeterinarianId(UUID veterinarianId) {
        return repository.findByVeterinarianId(veterinarianId);
    }

    @Override
    public void delete(VetAddress address) {
        repository.delete(address);
    }
}
