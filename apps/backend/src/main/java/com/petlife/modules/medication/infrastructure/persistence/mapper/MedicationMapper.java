package com.petlife.modules.medication.infrastructure.persistence.mapper;

import com.petlife.modules.medication.domain.entity.Medication;
import com.petlife.modules.medication.infrastructure.persistence.entity.MedicationJpaEntity;
import com.petlife.modules.pet.infrastructure.persistence.entity.PetJpaEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class MedicationMapper {

    private final MedicationAdministrationMapper administrationMapper;

    public MedicationMapper(MedicationAdministrationMapper administrationMapper) {
        this.administrationMapper = administrationMapper;
    }

    public Medication toDomain(MedicationJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        Medication domain = new Medication();
        domain.setId(entity.getId());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());
        if (entity.getPetEntity() != null) {
            domain.setPetId(entity.getPetEntity().getId());
            if (entity.getPetEntity().getUser() != null) {
                domain.setPetOwnerId(entity.getPetEntity().getUser().getId());
            }
        }
        domain.setName(entity.getName());
        domain.setDosage(entity.getDosage());
        domain.setFrequency(entity.getFrequency());
        domain.setMedicationType(entity.getMedicationType());
        domain.setCustomFrequencyHours(entity.getCustomFrequencyHours());
        domain.setStartDate(entity.getStartDate());
        domain.setEndDate(entity.getEndDate());
        domain.setTimesOfDay(new ArrayList<>(entity.getTimesOfDay()));
        domain.setStatus(entity.getStatus());

        if (entity.getAdministrations() != null) {
            domain.setAdministrations(entity.getAdministrations().stream()
                    .map(administrationMapper::toDomain)
                    .collect(Collectors.toList()));
        }

        return domain;
    }

    public MedicationJpaEntity toEntity(Medication domain) {
        if (domain == null) {
            return null;
        }

        MedicationJpaEntity entity = new MedicationJpaEntity();
        entity.setId(domain.getId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        
        if (domain.getPetId() != null) {
            PetJpaEntity pet = new PetJpaEntity();
            pet.setId(domain.getPetId());
            entity.setPetEntity(pet);
        }

        entity.setName(domain.getName());
        entity.setDosage(domain.getDosage());
        entity.setFrequency(domain.getFrequency());
        entity.setMedicationType(domain.getMedicationType());
        entity.setCustomFrequencyHours(domain.getCustomFrequencyHours());
        entity.setStartDate(domain.getStartDate());
        entity.setEndDate(domain.getEndDate());
        entity.setTimesOfDay(new ArrayList<>(domain.getTimesOfDay()));
        entity.setStatus(domain.getStatus());

        if (domain.getAdministrations() != null) {
            entity.setAdministrations(domain.getAdministrations().stream()
                    .map(admin -> {
                        var adminEntity = administrationMapper.toEntity(admin);
                        adminEntity.setMedication(entity);
                        return adminEntity;
                    })
                    .collect(Collectors.toList()));
        }

        return entity;
    }
}
