package com.petlife.modules.auth.infrastructure.persistence;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Adaptador de Persistência (Persistence Adapter / Output Adapter) na Arquitetura Hexagonal.
 * Implementa a porta de saída UserRepositoryPort utilizando Spring Data JPA / Hibernate.
 * Localiza-se na camada de infraestrutura — não deve ser injetado diretamente nos Use Cases.
 */
@Repository
public interface UserJpaRepository extends JpaRepository<User, UUID>, UserRepositoryPort {
    // Spring Data JPA implementa automaticamente: findById, findByEmail, existsByEmail, save, delete
}
