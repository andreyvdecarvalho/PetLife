package com.petlife.modules.pet.infrastructure.persistence;

import com.petlife.modules.pet.infrastructure.persistence.entity.ConsultationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ConsultationJpaRepository extends JpaRepository<ConsultationJpaEntity, UUID> {
    List<ConsultationJpaEntity> findByPetIdOrderByDateDesc(UUID petId);
    List<ConsultationJpaEntity> findByDateBetween(OffsetDateTime start, OffsetDateTime end);
    List<ConsultationJpaEntity> findByFollowUpDate(LocalDate followUpDate);
}
