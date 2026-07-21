package com.petlife.modules.pet.infrastructure.persistence.mapper;

import com.petlife.modules.auth.infrastructure.persistence.mapper.UserMapper;
import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.modules.pet.infrastructure.persistence.entity.PetJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class PetMapper {

    public PetMapper() {
    }

    public Pet toDomain(PetJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        Pet pet = new Pet();
        pet.setId(jpaEntity.getId());
        pet.setCreatedAt(jpaEntity.getCreatedAt());
        pet.setUpdatedAt(jpaEntity.getUpdatedAt());

        pet.setUser(UserMapper.toDomain(jpaEntity.getUser()));
        pet.setName(jpaEntity.getName());
        pet.setSpecies(jpaEntity.getSpecies());
        pet.setBreed(jpaEntity.getBreed());
        pet.setSex(jpaEntity.getSex());
        pet.setBirthDate(jpaEntity.getBirthDate());
        pet.setWeightKg(jpaEntity.getWeightKg());
        pet.setSize(jpaEntity.getSize());
        pet.setNeutered(jpaEntity.isNeutered());
        pet.setMicrochipId(jpaEntity.getMicrochipId());
        pet.setAllergies(jpaEntity.getAllergies());
        pet.setNotes(jpaEntity.getNotes());
        pet.setPhotoUrl(jpaEntity.getPhotoUrl());
        pet.setStatus(jpaEntity.getStatus());

        return pet;
    }

    public PetJpaEntity toJpaEntity(Pet domain) {
        if (domain == null) {
            return null;
        }

        PetJpaEntity jpaEntity = new PetJpaEntity();
        jpaEntity.setId(domain.getId());
        jpaEntity.setCreatedAt(domain.getCreatedAt());
        jpaEntity.setUpdatedAt(domain.getUpdatedAt());

        jpaEntity.setUser(UserMapper.toJpaEntity(domain.getUser()));
        jpaEntity.setName(domain.getName());
        jpaEntity.setSpecies(domain.getSpecies());
        jpaEntity.setBreed(domain.getBreed());
        jpaEntity.setSex(domain.getSex());
        jpaEntity.setBirthDate(domain.getBirthDate());
        jpaEntity.setWeightKg(domain.getWeightKg());
        jpaEntity.setSize(domain.getSize());
        jpaEntity.setNeutered(domain.isNeutered());
        jpaEntity.setMicrochipId(domain.getMicrochipId());
        jpaEntity.setAllergies(domain.getAllergies());
        jpaEntity.setNotes(domain.getNotes());
        jpaEntity.setPhotoUrl(domain.getPhotoUrl());
        jpaEntity.setStatus(domain.getStatus());

        return jpaEntity;
    }
}
