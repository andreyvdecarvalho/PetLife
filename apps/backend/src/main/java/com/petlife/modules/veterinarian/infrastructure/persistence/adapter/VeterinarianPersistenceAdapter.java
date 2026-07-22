package com.petlife.modules.veterinarian.infrastructure.persistence.adapter;

import com.petlife.modules.veterinarian.application.port.VeterinarianRepositoryPort;
import com.petlife.modules.veterinarian.domain.entity.Veterinarian;
import com.petlife.modules.veterinarian.infrastructure.persistence.VeterinarianJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.petlife.modules.veterinarian.infrastructure.persistence.mapper.VeterinarianMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.petlife.modules.veterinarian.infrastructure.persistence.entity.VeterinarianJpaEntity;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VeterinarianPersistenceAdapter implements VeterinarianRepositoryPort {

    private final VeterinarianJpaRepository repository;

    @Override
    public Veterinarian save(Veterinarian veterinarian) {
        var jpa = VeterinarianMapper.toJpaEntity(veterinarian);
        return VeterinarianMapper.toDomain(repository.save(jpa));
    }

    @Override
    public Optional<Veterinarian> findById(UUID id) {
        return repository.findById(id).map(VeterinarianMapper::toDomain);
    }

    @Override
    public Optional<Veterinarian> findByUserId(UUID userId) {
        return repository.findByUserId(userId).map(VeterinarianMapper::toDomain);
    }

    @Override
    public boolean existsByCrmvNumber(String crmvNumber) {
        return repository.existsByCrmvNumber(crmvNumber);
    }

    @Override
    public com.petlife.shared.domain.PageResult<Veterinarian> search(
            com.petlife.modules.veterinarian.infrastructure.dto.request.SearchVeterinariansRequest request) {
        Page<VeterinarianJpaEntity> page = repository.searchVeterinarians(
                request.latitude(),
                request.longitude(),
                request.radiusKm(),
                request.modality() != null ? request.modality().name() : null,
                request.specialty(),
                request.emergencyOnDuty(),
                PageRequest.of(request.page(), request.size())
        );
        var content = page.map(VeterinarianMapper::toDomain).getContent();
        return new com.petlife.shared.domain.PageResult<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
