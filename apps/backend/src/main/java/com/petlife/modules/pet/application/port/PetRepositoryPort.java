package com.petlife.modules.pet.application.port;

import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.entity.PetStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PetRepositoryPort {
    Pet save(Pet pet);
    Optional<Pet> findById(UUID id);
    long countByUserIdAndStatusNot(UUID userId, PetStatus status);
    List<Pet> findByUserIdAndStatusNot(UUID userId, PetStatus status);
}
