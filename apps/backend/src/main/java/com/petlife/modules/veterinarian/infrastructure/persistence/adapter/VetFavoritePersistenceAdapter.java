package com.petlife.modules.veterinarian.infrastructure.persistence.adapter;

import com.petlife.modules.veterinarian.application.port.VetFavoriteRepositoryPort;
import com.petlife.modules.veterinarian.domain.entity.VetFavorite;
import com.petlife.modules.veterinarian.infrastructure.persistence.VetFavoriteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VetFavoritePersistenceAdapter implements VetFavoriteRepositoryPort {

    private final VetFavoriteJpaRepository repository;

    @Override
    public VetFavorite save(VetFavorite favorite) {
        return com.petlife.modules.veterinarian.infrastructure.persistence.mapper.VeterinarianMapper.toDomain(repository.save(com.petlife.modules.veterinarian.infrastructure.persistence.mapper.VeterinarianMapper.toJpaEntity(favorite)));
    }

    @Override
    public List<VetFavorite> findByUserId(UUID userId) {
        return repository.findByUserId(userId).stream().map(com.petlife.modules.veterinarian.infrastructure.persistence.mapper.VeterinarianMapper::toDomain).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public Optional<VetFavorite> findByUserIdAndVeterinarianId(UUID userId, UUID veterinarianId) {
        return repository.findByUserIdAndVeterinarianId(userId, veterinarianId).map(com.petlife.modules.veterinarian.infrastructure.persistence.mapper.VeterinarianMapper::toDomain);
    }

    @Override
    public boolean existsByUserIdAndVeterinarianId(UUID userId, UUID veterinarianId) {
        return repository.existsByUserIdAndVeterinarianId(userId, veterinarianId);
    }

    @Override
    public void delete(VetFavorite favorite) {
        repository.delete(com.petlife.modules.veterinarian.infrastructure.persistence.mapper.VeterinarianMapper.toJpaEntity(favorite));
    }
}
