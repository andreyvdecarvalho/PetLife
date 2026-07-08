package com.petlife.modules.pet.application.port;

import com.petlife.modules.pet.entity.Grooming;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroomingRepositoryPort {
    Grooming save(Grooming grooming);
    Optional<Grooming> findById(UUID id);
    List<Grooming> findAllByPetId(UUID petId);
    void delete(Grooming grooming);
}
