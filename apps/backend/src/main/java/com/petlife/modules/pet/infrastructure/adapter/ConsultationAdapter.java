package com.petlife.modules.pet.infrastructure.adapter;

import com.petlife.modules.pet.application.port.ConsultationRepositoryPort;
import com.petlife.modules.pet.entity.Consultation;
import com.petlife.modules.pet.infrastructure.persistence.ConsultationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ConsultationAdapter implements ConsultationRepositoryPort {

    private final ConsultationJpaRepository repository;

    @Override
    public Consultation save(Consultation consultation) {
        return repository.save(consultation);
    }

    @Override
    public Optional<Consultation> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Consultation> findAllByPetId(UUID petId) {
        return repository.findByPetIdOrderByDateDesc(petId);
    }

    @Override
    public void delete(Consultation consultation) {
        repository.delete(consultation);
    }
}
