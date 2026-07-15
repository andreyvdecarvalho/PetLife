package com.petlife.modules.veterinarian.infrastructure.persistence;

import com.petlife.modules.veterinarian.entity.VetAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VetAddressJpaRepository extends JpaRepository<VetAddress, UUID> {
    List<VetAddress> findByVeterinarianId(UUID veterinarianId);
    Optional<VetAddress> findByIdAndVeterinarianId(UUID id, UUID veterinarianId);
}
