package com.petlife.modules.pet.infrastructure.persistence.mapper;

import com.petlife.modules.pet.domain.entity.Vaccination;
import com.petlife.modules.pet.infrastructure.persistence.entity.VaccinationJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class VaccinationMapper {

    private final PetMapper petMapper;

    public VaccinationMapper(PetMapper petMapper) {
        this.petMapper = petMapper;
    }

    public Vaccination toDomain(VaccinationJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        Vaccination domain = new Vaccination();
        domain.setId(jpaEntity.getId());
        domain.setPet(petMapper.toDomain(jpaEntity.getPet()));
        domain.setVaccineName(jpaEntity.getVaccineName());
        domain.setDateAdministered(jpaEntity.getDateAdministered());
        domain.setNextDoseDate(jpaEntity.getNextDoseDate());
        domain.setVeterinarian(jpaEntity.getVeterinarian());
        domain.setClinic(jpaEntity.getClinic());
        domain.setBatchNumber(jpaEntity.getBatchNumber());
        domain.setManufacturer(jpaEntity.getManufacturer());
        domain.setProofUrl(jpaEntity.getProofUrl());
        domain.setNotes(jpaEntity.getNotes());
        domain.setReminderActive(jpaEntity.isReminderActive());
        domain.setCreatedAt(jpaEntity.getCreatedAt());
        domain.setUpdatedAt(jpaEntity.getUpdatedAt());

        return domain;
    }

    public VaccinationJpaEntity toJpaEntity(Vaccination domain) {
        if (domain == null) {
            return null;
        }

        VaccinationJpaEntity jpaEntity = new VaccinationJpaEntity();
        jpaEntity.setId(domain.getId());
        jpaEntity.setPet(petMapper.toJpaEntity(domain.getPet()));
        jpaEntity.setVaccineName(domain.getVaccineName());
        jpaEntity.setDateAdministered(domain.getDateAdministered());
        jpaEntity.setNextDoseDate(domain.getNextDoseDate());
        jpaEntity.setVeterinarian(domain.getVeterinarian());
        jpaEntity.setClinic(domain.getClinic());
        jpaEntity.setBatchNumber(domain.getBatchNumber());
        jpaEntity.setManufacturer(domain.getManufacturer());
        jpaEntity.setProofUrl(domain.getProofUrl());
        jpaEntity.setNotes(domain.getNotes());
        jpaEntity.setReminderActive(domain.isReminderActive());
        jpaEntity.setCreatedAt(domain.getCreatedAt());
        jpaEntity.setUpdatedAt(domain.getUpdatedAt());

        return jpaEntity;
    }
}
