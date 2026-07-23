package com.petlife.modules.pet.application.port;

import com.petlife.modules.pet.domain.entity.Consultation;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultationRepositoryPort {
    Consultation save(Consultation consultation);
    Optional<Consultation> findById(UUID id);
    List<Consultation> findAllByPetId(UUID petId);
    void delete(Consultation consultation);
    List<Consultation> findByDateBetween(OffsetDateTime start, OffsetDateTime end);
    List<Consultation> findByFollowUpDate(LocalDate followUpDate);
}
