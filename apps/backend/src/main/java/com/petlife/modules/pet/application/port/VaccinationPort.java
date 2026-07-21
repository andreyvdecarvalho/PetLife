package com.petlife.modules.pet.application.port;

import com.petlife.modules.pet.domain.entity.Vaccination;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VaccinationPort {
    Vaccination save(Vaccination vaccination);
    List<Vaccination> findByPetId(UUID petId);
    Optional<Vaccination> findById(UUID id);
    void delete(Vaccination vaccination);
    List<Vaccination> findByReminderActiveTrueAndNextDoseDate(LocalDate nextDoseDate);
}
