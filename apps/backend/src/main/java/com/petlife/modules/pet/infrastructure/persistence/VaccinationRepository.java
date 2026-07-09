package com.petlife.modules.pet.infrastructure.persistence;

import com.petlife.modules.pet.entity.Vaccination;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VaccinationRepository extends JpaRepository<Vaccination, UUID> {
    List<Vaccination> findByPetIdOrderByDateAdministeredDesc(UUID petId);
    List<Vaccination> findByReminderActiveTrueAndNextDoseDate(LocalDate nextDoseDate);
}
