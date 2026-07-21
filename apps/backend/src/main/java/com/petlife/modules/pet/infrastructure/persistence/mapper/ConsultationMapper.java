package com.petlife.modules.pet.infrastructure.persistence.mapper;

import com.petlife.modules.pet.domain.entity.Consultation;
import com.petlife.modules.pet.infrastructure.persistence.entity.ConsultationJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ConsultationMapper {

    private final PetMapper petMapper;

    public ConsultationMapper(PetMapper petMapper) {
        this.petMapper = petMapper;
    }

    public Consultation toDomain(ConsultationJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        Consultation domain = new Consultation();
        domain.setId(jpaEntity.getId());
        domain.setPet(petMapper.toDomain(jpaEntity.getPet()));
        domain.setDate(jpaEntity.getDate());
        domain.setVeterinarian(jpaEntity.getVeterinarian());
        domain.setClinic(jpaEntity.getClinic());
        domain.setReason(jpaEntity.getReason());
        domain.setDiagnosis(jpaEntity.getDiagnosis());
        domain.setPrescriptions(jpaEntity.getPrescriptions());
        domain.setNotes(jpaEntity.getNotes());
        domain.setWeightAtVisit(jpaEntity.getWeightAtVisit());
        domain.setFollowUpDate(jpaEntity.getFollowUpDate());
        domain.setCost(jpaEntity.getCost());
        domain.setAttachments(jpaEntity.getAttachments());
        domain.setCreatedAt(jpaEntity.getCreatedAt());
        domain.setUpdatedAt(jpaEntity.getUpdatedAt());

        return domain;
    }

    public ConsultationJpaEntity toJpaEntity(Consultation domain) {
        if (domain == null) {
            return null;
        }

        ConsultationJpaEntity jpaEntity = new ConsultationJpaEntity();
        jpaEntity.setId(domain.getId());
        jpaEntity.setPet(petMapper.toJpaEntity(domain.getPet()));
        jpaEntity.setDate(domain.getDate());
        jpaEntity.setVeterinarian(domain.getVeterinarian());
        jpaEntity.setClinic(domain.getClinic());
        jpaEntity.setReason(domain.getReason());
        jpaEntity.setDiagnosis(domain.getDiagnosis());
        jpaEntity.setPrescriptions(domain.getPrescriptions());
        jpaEntity.setNotes(domain.getNotes());
        jpaEntity.setWeightAtVisit(domain.getWeightAtVisit());
        jpaEntity.setFollowUpDate(domain.getFollowUpDate());
        jpaEntity.setCost(domain.getCost());
        jpaEntity.setAttachments(domain.getAttachments());
        jpaEntity.setCreatedAt(domain.getCreatedAt());
        jpaEntity.setUpdatedAt(domain.getUpdatedAt());

        return jpaEntity;
    }
}
