package com.petlife.modules.pet.infrastructure.adapter;

import com.petlife.modules.pet.application.port.ConsultationRepositoryPort;
import com.petlife.modules.pet.domain.entity.Consultation;
import com.petlife.modules.pet.infrastructure.persistence.ConsultationJpaRepository;
import com.petlife.modules.pet.infrastructure.persistence.mapper.ConsultationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ConsultationAdapter implements ConsultationRepositoryPort {

    private final ConsultationJpaRepository repository;
    private final ConsultationMapper mapper;

    @Override
    public Consultation save(Consultation consultation) {
        return mapper.toDomain(repository.save(mapper.toJpaEntity(consultation)));
    }

    @Override
    public Optional<Consultation> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Consultation> findAllByPetId(UUID petId) {
        return repository.findByPetIdOrderByDateDesc(petId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Consultation consultation) {
        repository.delete(mapper.toJpaEntity(consultation));
    }

    @Override
    public List<Consultation> findByDateBetween(java.time.OffsetDateTime start, java.time.OffsetDateTime end) {
        return repository.findByDateBetween(start, end).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Consultation> findByFollowUpDate(java.time.LocalDate followUpDate) {
        return repository.findByFollowUpDate(followUpDate).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
