package com.petlife.modules.pet.infrastructure.persistence.mapper;

import com.petlife.modules.pet.domain.entity.WeightRecord;
import com.petlife.modules.pet.infrastructure.persistence.entity.WeightRecordJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class WeightRecordMapper {

    private final PetMapper petMapper;

    public WeightRecordMapper(PetMapper petMapper) {
        this.petMapper = petMapper;
    }

    public WeightRecord toDomain(WeightRecordJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        WeightRecord domain = new WeightRecord();
        domain.setId(jpaEntity.getId());
        domain.setPet(petMapper.toDomain(jpaEntity.getPet()));
        domain.setWeightKg(jpaEntity.getWeightKg());
        domain.setRecordedAt(jpaEntity.getRecordedAt());

        return domain;
    }

    public WeightRecordJpaEntity toJpaEntity(WeightRecord domain) {
        if (domain == null) {
            return null;
        }

        WeightRecordJpaEntity jpaEntity = new WeightRecordJpaEntity();
        jpaEntity.setId(domain.getId());
        jpaEntity.setPet(petMapper.toJpaEntity(domain.getPet()));
        jpaEntity.setWeightKg(domain.getWeightKg());
        jpaEntity.setRecordedAt(domain.getRecordedAt());

        return jpaEntity;
    }
}
