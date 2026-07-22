package com.petlife.modules.veterinarian.infrastructure.persistence.adapter;

import com.petlife.modules.veterinarian.application.port.VetAddressRepositoryPort;
import com.petlife.modules.veterinarian.domain.entity.VetAddress;
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
        return com.petlife.modules.veterinarian.infrastructure.persistence.mapper.VeterinarianMapper.toDomain(repository.save(com.petlife.modules.veterinarian.infrastructure.persistence.mapper.VeterinarianMapper.toJpaEntity(address)));
    }

    @Override
    public Optional<VetAddress> findByIdAndVeterinarianId(UUID id, UUID veterinarianId) {
        return repository.findByIdAndVeterinarianId(id, veterinarianId).map(com.petlife.modules.veterinarian.infrastructure.persistence.mapper.VeterinarianMapper::toDomain);
    }

    @Override
    public List<VetAddress> findByVeterinarianId(UUID veterinarianId) {
        return repository.findByVeterinarianId(veterinarianId).stream().map(com.petlife.modules.veterinarian.infrastructure.persistence.mapper.VeterinarianMapper::toDomain).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void delete(VetAddress address) {
        repository.delete(com.petlife.modules.veterinarian.infrastructure.persistence.mapper.VeterinarianMapper.toJpaEntity(address));
    }
}
