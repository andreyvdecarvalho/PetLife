package com.petlife.modules.pet.infrastructure.persistence;

import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface PetJpaRepository extends JpaRepository<Pet, UUID>, PetRepositoryPort {

    @Query("SELECT p FROM Pet p JOIN FETCH p.user u WHERE p.birthDate IS NOT NULL "
            + "AND EXTRACT(MONTH FROM p.birthDate) = :month "
            + "AND EXTRACT(DAY FROM p.birthDate) = :day")
    List<Pet> findPetsByBirthday(int month, int day);
}
