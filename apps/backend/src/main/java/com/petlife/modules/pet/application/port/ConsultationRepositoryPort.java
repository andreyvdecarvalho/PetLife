package com.petlife.modules.pet.application.port;

import com.petlife.modules.pet.entity.Consultation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultationRepositoryPort {
    Consultation save(Consultation consultation);
    Optional<Consultation> findById(UUID id);
    List<Consultation> findAllByPetId(UUID petId);
    void delete(Consultation consultation);
}
