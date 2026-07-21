package com.petlife.modules.pet.infrastructure.persistence;

import com.petlife.modules.pet.infrastructure.persistence.entity.PetJpaEntity;
import com.petlife.modules.pet.domain.entity.PetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface PetJpaRepository extends JpaRepository<PetJpaEntity, UUID> {

    @Query("SELECT p FROM PetJpaEntity p JOIN FETCH p.user u WHERE p.birthDate IS NOT NULL "
            + "AND EXTRACT(MONTH FROM p.birthDate) = :month "
            + "AND EXTRACT(DAY FROM p.birthDate) = :day")
    List<PetJpaEntity> findPetsByBirthday(int month, int day);

    long countByUserIdAndStatusNot(UUID userId, PetStatus status);
    
    List<PetJpaEntity> findByUserIdAndStatusNot(UUID userId, PetStatus status);
    
    Page<PetJpaEntity> findByUserIdAndStatusNot(UUID userId, PetStatus status, Pageable pageable);
    
    Page<PetJpaEntity> findByUserIdAndStatus(UUID userId, PetStatus status, Pageable pageable);
}
