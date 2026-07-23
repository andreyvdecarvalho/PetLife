package com.petlife.modules.auth.infrastructure.persistence.adapter;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.auth.infrastructure.persistence.UserJpaRepository;
import com.petlife.modules.auth.infrastructure.persistence.entity.UserJpaEntity;
import com.petlife.modules.auth.infrastructure.persistence.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository repository;

    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        UserJpaEntity jpaEntity = UserMapper.toJpaEntity(user);
        UserJpaEntity saved = repository.save(jpaEntity);
        user.setId(saved.getId());
        user.setCreatedAt(saved.getCreatedAt());
        user.setUpdatedAt(saved.getUpdatedAt());
        return user;
    }

    @Override
    public void delete(User user) {
        UserJpaEntity jpaEntity = UserMapper.toJpaEntity(user);
        repository.delete(jpaEntity);
    }
}
