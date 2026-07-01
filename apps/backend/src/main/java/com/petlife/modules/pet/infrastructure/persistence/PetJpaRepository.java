package com.petlife.modules.pet.infrastructure.persistence;

import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.entity.PetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface PetJpaRepository extends JpaRepository<Pet, UUID>, PetRepositoryPort {
}
