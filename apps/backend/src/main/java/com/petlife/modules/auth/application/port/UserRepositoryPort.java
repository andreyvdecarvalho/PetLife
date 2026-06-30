package com.petlife.modules.auth.application.port;

import com.petlife.modules.auth.entity.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Port (interface de domínio) para operações de persistência do User.
 * Use Cases dependem desta interface, nunca do JpaRepository diretamente.
 * Princípio: Dependency Inversion (DIP) — Clean Architecture.
 */
public interface UserRepositoryPort {

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    User save(User user);

    void delete(User user);
}
