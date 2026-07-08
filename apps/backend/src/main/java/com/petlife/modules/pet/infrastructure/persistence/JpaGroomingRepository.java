package com.petlife.modules.pet.infrastructure.persistence;

import com.petlife.modules.pet.entity.Grooming;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaGroomingRepository extends JpaRepository<Grooming, UUID> {
    List<Grooming> findByPetIdOrderByDateDesc(UUID petId);
}
