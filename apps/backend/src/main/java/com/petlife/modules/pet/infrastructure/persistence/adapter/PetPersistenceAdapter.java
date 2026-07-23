package com.petlife.modules.pet.infrastructure.persistence.adapter;

import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.modules.pet.domain.entity.PetStatus;
import com.petlife.modules.pet.infrastructure.persistence.PetJpaRepository;
import com.petlife.modules.pet.infrastructure.persistence.entity.PetJpaEntity;
import com.petlife.modules.pet.infrastructure.persistence.mapper.PetMapper;
import com.petlife.shared.domain.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PetPersistenceAdapter implements PetRepositoryPort {

    private final PetJpaRepository repository;
    private final PetMapper mapper;

    @Override
    public Pet save(Pet pet) {
        PetJpaEntity entity = mapper.toJpaEntity(pet);
        PetJpaEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Pet> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public long countByUserIdAndStatusNot(UUID userId, PetStatus status) {
        return repository.countByUserIdAndStatusNot(userId, status);
    }

    @Override
    public List<Pet> findByUserIdAndStatusNot(UUID userId, PetStatus status) {
        return repository.findByUserIdAndStatusNot(userId, status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<Pet> findByUserIdAndStatusNot(UUID userId, PetStatus status, int page, int size) {
        Page<PetJpaEntity> entityPage = repository.findByUserIdAndStatusNot(userId, status, PageRequest.of(page, size));
        return new PageResult<>(
                entityPage.getContent().stream().map(mapper::toDomain).collect(Collectors.toList()),
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages()
        );
    }

    @Override
    public PageResult<Pet> findByUserIdAndStatus(UUID userId, PetStatus status, int page, int size) {
        Page<PetJpaEntity> entityPage = repository.findByUserIdAndStatus(userId, status, PageRequest.of(page, size));
        return new PageResult<>(
                entityPage.getContent().stream().map(mapper::toDomain).collect(Collectors.toList()),
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages()
        );
    }

    @Override
    public void delete(Pet pet) {
        repository.delete(mapper.toJpaEntity(pet));
    }

    @Override
    public List<Pet> findPetsByBirthday(int month, int day) {
        return repository.findPetsByBirthday(month, day).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
