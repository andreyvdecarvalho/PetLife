package com.petlife.modules.medication.infrastructure.persistence;

import com.petlife.modules.medication.application.port.MedicationRepositoryPort;
import com.petlife.modules.medication.domain.entity.Medication;
import com.petlife.modules.medication.domain.entity.MedicationStatus;
import com.petlife.modules.medication.infrastructure.persistence.entity.MedicationJpaEntity;
import com.petlife.modules.medication.infrastructure.persistence.mapper.MedicationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MedicationPersistenceAdapter implements MedicationRepositoryPort {

    private final MedicationJpaRepository repository;
    private final MedicationMapper mapper;

    @Override
    public Medication save(Medication medication) {
        MedicationJpaEntity entity = mapper.toEntity(medication);
        MedicationJpaEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Medication> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Medication> findByPetId(UUID petId) {
        return repository.findByPetEntityId(petId).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Medication> findByPetIdAndStatus(UUID petId, MedicationStatus status) {
        return repository.findByPetEntityIdAndStatus(petId, status).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }
}
