package com.petlife.modules.pet.infrastructure.persistence.mapper;

import com.petlife.modules.pet.domain.entity.Grooming;
import com.petlife.modules.pet.infrastructure.persistence.entity.GroomingJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class GroomingMapper {

    private final PetMapper petMapper;

    public GroomingMapper(PetMapper petMapper) {
        this.petMapper = petMapper;
    }

    public Grooming toDomain(GroomingJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        Grooming domain = new Grooming();
        domain.setId(jpaEntity.getId());
        domain.setPet(petMapper.toDomain(jpaEntity.getPet()));
        domain.setType(jpaEntity.getType());
        domain.setDate(jpaEntity.getDate());
        domain.setProvider(jpaEntity.getProvider());
        domain.setCost(jpaEntity.getCost());
        domain.setFrequencyDays(jpaEntity.getFrequencyDays());
        domain.setNextDate(jpaEntity.getNextDate());
        domain.setNotes(jpaEntity.getNotes());
        domain.setPhotos(jpaEntity.getPhotos());
        domain.setCreatedAt(jpaEntity.getCreatedAt());
        domain.setUpdatedAt(jpaEntity.getUpdatedAt());

        return domain;
    }

    public GroomingJpaEntity toJpaEntity(Grooming domain) {
        if (domain == null) {
            return null;
        }

        GroomingJpaEntity jpaEntity = new GroomingJpaEntity();
        jpaEntity.setId(domain.getId());
        jpaEntity.setPet(petMapper.toJpaEntity(domain.getPet()));
        jpaEntity.setType(domain.getType());
        jpaEntity.setDate(domain.getDate());
        jpaEntity.setProvider(domain.getProvider());
        jpaEntity.setCost(domain.getCost());
        jpaEntity.setFrequencyDays(domain.getFrequencyDays());
        jpaEntity.setNextDate(domain.getNextDate());
        jpaEntity.setNotes(domain.getNotes());
        jpaEntity.setPhotos(domain.getPhotos());
        jpaEntity.setCreatedAt(domain.getCreatedAt());
        jpaEntity.setUpdatedAt(domain.getUpdatedAt());

        return jpaEntity;
    }
}
