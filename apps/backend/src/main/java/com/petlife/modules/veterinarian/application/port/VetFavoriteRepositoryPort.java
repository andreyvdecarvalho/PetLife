package com.petlife.modules.veterinarian.application.port;

import com.petlife.modules.veterinarian.entity.VetFavorite;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VetFavoriteRepositoryPort {
    VetFavorite save(VetFavorite favorite);
    List<VetFavorite> findByUserId(UUID userId);
    Optional<VetFavorite> findByUserIdAndVeterinarianId(UUID userId, UUID veterinarianId);
    boolean existsByUserIdAndVeterinarianId(UUID userId, UUID veterinarianId);
    void delete(VetFavorite favorite);
}
