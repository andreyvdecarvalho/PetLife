package com.petlife.modules.medication.infrastructure.persistence.mapper;

import com.petlife.modules.medication.domain.entity.MedicationAdministration;
import com.petlife.modules.medication.infrastructure.persistence.entity.MedicationAdministrationJpaEntity;
import org.springframework.stereotype.Component;


@Component
public class MedicationAdministrationMapper {

    public MedicationAdministration toDomain(MedicationAdministrationJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        MedicationAdministration domain = new MedicationAdministration();
        domain.setId(entity.getId());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());
        domain.setScheduledTime(entity.getScheduledTime());
        domain.setAdministeredAt(entity.getAdministeredAt());
        domain.setStatus(entity.getStatus());
        domain.setSkippedReason(entity.getSkippedReason());

        if (entity.getMedication() != null) {
            domain.setMedicationId(entity.getMedication().getId());
            domain.setMedicationName(entity.getMedication().getName());
            if (entity.getMedication().getPetEntity() != null 
                    && entity.getMedication().getPetEntity().getUser() != null) {
                domain.setPetOwnerId(entity.getMedication().getPetEntity().getUser().getId());
            }
        }

        return domain;
    }

    public MedicationAdministrationJpaEntity toEntity(MedicationAdministration domain) {
        if (domain == null) {
            return null;
        }

        MedicationAdministrationJpaEntity entity = new MedicationAdministrationJpaEntity();
        entity.setId(domain.getId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setScheduledTime(domain.getScheduledTime());
        entity.setAdministeredAt(domain.getAdministeredAt());
        entity.setStatus(domain.getStatus());
        entity.setSkippedReason(domain.getSkippedReason());

        if (domain.getMedicationId() != null) {
            com.petlife.modules.medication.infrastructure.persistence.entity.MedicationJpaEntity med = new com.petlife.modules.medication.infrastructure.persistence.entity.MedicationJpaEntity();
            med.setId(domain.getMedicationId());
            if (domain.getMedicationName() != null) {
                med.setName(domain.getMedicationName()); 
            }
            if (domain.getPetOwnerId() != null) {
                var pet = new com.petlife.modules.pet.infrastructure.persistence.entity.PetJpaEntity();
                var user = new com.petlife.modules.auth.infrastructure.persistence.entity.UserJpaEntity();
                user.setId(domain.getPetOwnerId());
                pet.setUser(user);
                med.setPetEntity(pet);
            }
            entity.setMedication(med);
        }

        return entity;
    }
}
