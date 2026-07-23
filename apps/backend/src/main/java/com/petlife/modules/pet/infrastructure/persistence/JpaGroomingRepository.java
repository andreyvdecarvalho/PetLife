package com.petlife.modules.pet.infrastructure.persistence;

import com.petlife.modules.pet.infrastructure.persistence.entity.GroomingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaGroomingRepository extends JpaRepository<GroomingJpaEntity, UUID> {
    List<GroomingJpaEntity> findByPetIdOrderByDateDesc(UUID petId);
    List<GroomingJpaEntity> findByNextDate(LocalDate nextDate);
}
