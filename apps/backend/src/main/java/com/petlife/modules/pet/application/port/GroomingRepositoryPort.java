package com.petlife.modules.pet.application.port;

import com.petlife.modules.pet.domain.entity.Grooming;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroomingRepositoryPort {
    Grooming save(Grooming grooming);
    Optional<Grooming> findById(UUID id);
    List<Grooming> findAllByPetId(UUID petId);
    void delete(Grooming grooming);
    List<Grooming> findByNextDate(LocalDate nextDate);
}
