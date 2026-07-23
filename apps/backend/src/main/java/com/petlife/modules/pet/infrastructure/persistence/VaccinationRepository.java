package com.petlife.modules.pet.infrastructure.persistence;

import com.petlife.modules.pet.infrastructure.persistence.entity.VaccinationJpaEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VaccinationRepository extends JpaRepository<VaccinationJpaEntity, UUID> {
    List<VaccinationJpaEntity> findByPetIdOrderByDateAdministeredDesc(UUID petId);
    List<VaccinationJpaEntity> findByReminderActiveTrueAndNextDoseDate(LocalDate nextDoseDate);
}
