package com.petlife.modules.pet.infrastructure.persistence;

import com.petlife.modules.pet.application.port.VaccinationPort;
import com.petlife.modules.pet.domain.entity.Vaccination;
import com.petlife.modules.pet.infrastructure.persistence.mapper.VaccinationMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VaccinationAdapter implements VaccinationPort {

    private final VaccinationRepository repository;
    private final VaccinationMapper mapper;

    @Override
    public Vaccination save(Vaccination vaccination) {
        return mapper.toDomain(repository.save(mapper.toJpaEntity(vaccination)));
    }

    @Override
    public List<Vaccination> findByPetId(UUID petId) {
        return repository.findByPetIdOrderByDateAdministeredDesc(petId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Vaccination> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void delete(Vaccination vaccination) {
        repository.delete(mapper.toJpaEntity(vaccination));
    }

    @Override
    public List<Vaccination> findByReminderActiveTrueAndNextDoseDate(java.time.LocalDate nextDoseDate) {
        return repository.findByReminderActiveTrueAndNextDoseDate(nextDoseDate).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
