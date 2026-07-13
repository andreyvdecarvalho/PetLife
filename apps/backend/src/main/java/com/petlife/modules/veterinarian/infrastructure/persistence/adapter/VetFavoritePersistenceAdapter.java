package com.petlife.modules.veterinarian.infrastructure.persistence.adapter;

import com.petlife.modules.veterinarian.application.port.VetFavoriteRepositoryPort;
import com.petlife.modules.veterinarian.entity.VetFavorite;
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
        return repository.save(favorite);
    }

    @Override
    public List<VetFavorite> findByUserId(UUID userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public Optional<VetFavorite> findByUserIdAndVeterinarianId(UUID userId, UUID veterinarianId) {
        return repository.findByUserIdAndVeterinarianId(userId, veterinarianId);
    }

    @Override
    public boolean existsByUserIdAndVeterinarianId(UUID userId, UUID veterinarianId) {
        return repository.existsByUserIdAndVeterinarianId(userId, veterinarianId);
    }

    @Override
    public void delete(VetFavorite favorite) {
        repository.delete(favorite);
    }
}
