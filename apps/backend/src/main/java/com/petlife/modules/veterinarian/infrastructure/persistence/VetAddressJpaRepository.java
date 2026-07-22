package com.petlife.modules.veterinarian.infrastructure.persistence;

import com.petlife.modules.veterinarian.infrastructure.persistence.entity.VetAddressJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VetAddressJpaRepository extends JpaRepository<VetAddressJpaEntity, UUID> {
    List<VetAddressJpaEntity> findByVeterinarianId(UUID veterinarianId);
    Optional<VetAddressJpaEntity> findByIdAndVeterinarianId(UUID id, UUID veterinarianId);
}
