package com.petlife.modules.veterinarian.application.port;

import com.petlife.modules.veterinarian.entity.VetAddress;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VetAddressRepositoryPort {
    VetAddress save(VetAddress address);
    Optional<VetAddress> findByIdAndVeterinarianId(UUID id, UUID veterinarianId);
    List<VetAddress> findByVeterinarianId(UUID veterinarianId);
    void delete(VetAddress address);
}
