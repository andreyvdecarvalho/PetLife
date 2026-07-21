package com.petlife.modules.pet.infrastructure.persistence.mapper;

import com.petlife.modules.pet.domain.entity.RoutineActivity;
import com.petlife.modules.pet.infrastructure.persistence.entity.RoutineActivityJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RoutineActivityMapper {

    private final PetMapper petMapper;

    public RoutineActivityMapper(PetMapper petMapper) {
        this.petMapper = petMapper;
    }

    public RoutineActivity toDomain(RoutineActivityJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        RoutineActivity domain = new RoutineActivity();
        domain.setId(jpaEntity.getId());
        domain.setCreatedAt(jpaEntity.getCreatedAt());
        domain.setUpdatedAt(jpaEntity.getUpdatedAt());
        domain.setPet(petMapper.toDomain(jpaEntity.getPet()));
        domain.setTitle(jpaEntity.getTitle());
        domain.setDescription(jpaEntity.getDescription());
        domain.setActivityDate(jpaEntity.getActivityDate());
        domain.setActivityTime(jpaEntity.getActivityTime());
        domain.setType(jpaEntity.getType());
        domain.setStatus(jpaEntity.getStatus());

        return domain;
    }

    public RoutineActivityJpaEntity toJpaEntity(RoutineActivity domain) {
        if (domain == null) {
            return null;
        }

        RoutineActivityJpaEntity jpaEntity = new RoutineActivityJpaEntity();
        jpaEntity.setId(domain.getId());
        jpaEntity.setCreatedAt(domain.getCreatedAt());
        jpaEntity.setUpdatedAt(domain.getUpdatedAt());
        jpaEntity.setPet(petMapper.toJpaEntity(domain.getPet()));
        jpaEntity.setTitle(domain.getTitle());
        jpaEntity.setDescription(domain.getDescription());
        jpaEntity.setActivityDate(domain.getActivityDate());
        jpaEntity.setActivityTime(domain.getActivityTime());
        jpaEntity.setType(domain.getType());
        jpaEntity.setStatus(domain.getStatus());

        return jpaEntity;
    }
}
