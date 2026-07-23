package com.petlife.modules.veterinarian.infrastructure.persistence;

import com.petlife.modules.veterinarian.infrastructure.persistence.entity.VetFavoriteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VetFavoriteJpaRepository extends JpaRepository<VetFavoriteJpaEntity, UUID> {
    List<VetFavoriteJpaEntity> findByUserId(UUID userId);
    Optional<VetFavoriteJpaEntity> findByUserIdAndVeterinarianId(UUID userId, UUID veterinarianId);
    boolean existsByUserIdAndVeterinarianId(UUID userId, UUID veterinarianId);
}
