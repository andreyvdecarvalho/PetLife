package com.petlife.modules.auth.infrastructure.persistence;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Implementação do UserRepositoryPort via Spring Data JPA.
 * Camada de infraestrutura — não deve ser injetada em Use Cases diretamente.
 * Use Cases devem depender de UserRepositoryPort (interface), não desta classe.
 */
@Repository
public interface UserJpaRepository extends JpaRepository<User, UUID>, UserRepositoryPort {
    // Spring Data JPA implementa automaticamente: findById, findByEmail, existsByEmail, save, delete
}
