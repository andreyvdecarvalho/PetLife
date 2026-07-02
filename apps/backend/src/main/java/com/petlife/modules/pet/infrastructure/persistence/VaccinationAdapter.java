package com.petlife.modules.pet.infrastructure.persistence;

import com.petlife.modules.pet.application.port.VaccinationPort;
import com.petlife.modules.pet.entity.Vaccination;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class VaccinationAdapter implements VaccinationPort {

    private final VaccinationRepository repository;

    public VaccinationAdapter(VaccinationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Vaccination save(Vaccination vaccination) {
        return repository.save(vaccination);
    }

    @Override
    public List<Vaccination> findByPetId(UUID petId) {
        return repository.findByPetIdOrderByDateAdministeredDesc(petId);
    }

    @Override
    public Optional<Vaccination> findById(UUID id) {
        return repository.findById(id);
    }
}
