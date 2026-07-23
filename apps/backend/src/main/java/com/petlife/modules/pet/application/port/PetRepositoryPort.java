package com.petlife.modules.pet.application.port;

import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.modules.pet.domain.entity.PetStatus;
import com.petlife.shared.domain.PageResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PetRepositoryPort {
    Pet save(Pet pet);
    Optional<Pet> findById(UUID id);
    long countByUserIdAndStatusNot(UUID userId, PetStatus status);
    List<Pet> findByUserIdAndStatusNot(UUID userId, PetStatus status);
    PageResult<Pet> findByUserIdAndStatusNot(UUID userId, PetStatus status, int page, int size);
    PageResult<Pet> findByUserIdAndStatus(UUID userId, PetStatus status, int page, int size);
    void delete(Pet pet);
    List<Pet> findPetsByBirthday(int month, int day);
}
