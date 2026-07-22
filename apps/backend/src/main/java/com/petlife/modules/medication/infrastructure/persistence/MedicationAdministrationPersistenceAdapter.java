package com.petlife.modules.medication.infrastructure.persistence;

import com.petlife.modules.medication.application.port.MedicationAdministrationRepositoryPort;
import com.petlife.modules.medication.domain.entity.MedicationAdministration;
import com.petlife.modules.medication.domain.entity.MedicationAdministrationStatus;
import com.petlife.modules.medication.infrastructure.persistence.entity.MedicationAdministrationJpaEntity;
import com.petlife.modules.medication.infrastructure.persistence.mapper.MedicationAdministrationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MedicationAdministrationPersistenceAdapter implements MedicationAdministrationRepositoryPort {

    private final MedicationAdministrationJpaRepository repository;
    private final MedicationAdministrationMapper mapper;

    @Override
    public MedicationAdministration save(MedicationAdministration administration) {
        MedicationAdministrationJpaEntity entity = mapper.toEntity(administration);
        MedicationAdministrationJpaEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<MedicationAdministration> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void delete(MedicationAdministration administration) {
        repository.delete(mapper.toEntity(administration));
    }

    @Override
    public void deleteAll(List<MedicationAdministration> administrations) {
        List<MedicationAdministrationJpaEntity> entities = administrations.stream()
                .map(mapper::toEntity).collect(Collectors.toList());
        repository.deleteAll(entities);
    }

    @Override
    public List<MedicationAdministration> findByMedicationIdAndStatusAndScheduledTimeAfter(
            UUID medicationId, MedicationAdministrationStatus status, OffsetDateTime time) {
        return repository.findByMedicationIdAndStatusAndScheduledTimeAfter(medicationId, status, time)
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<MedicationAdministration> findByMedicationPetId(UUID petId) {
        return repository.findByMedicationPetId(petId)
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<MedicationAdministration> saveAll(List<MedicationAdministration> administrations) {
        List<MedicationAdministrationJpaEntity> entities = administrations.stream()
                .map(mapper::toEntity)
                .collect(Collectors.toList());
        List<MedicationAdministrationJpaEntity> saved = repository.saveAll(entities);
        return saved.stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<MedicationAdministration> findByStatusAndScheduledTimeBefore(
            MedicationAdministrationStatus status, OffsetDateTime time) {
        return repository.findByStatusAndScheduledTimeBefore(status, time)
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}
