package com.petlife.modules.auth.application.port;

import com.petlife.modules.auth.entity.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Port (Porta de Saída / Output Port) na Arquitetura Hexagonal.
 * Define o contrato de persistência do domínio para o User.
 * O Core da aplicação interage com o banco de dados exclusivamente através desta porta.
 * Use Cases dependem desta interface, nunca de adaptadores ou repositórios JPA concretos.
 * Princípios: Dependency Inversion (DIP) e isolamento da Arquitetura Hexagonal.
 */
public interface UserRepositoryPort {

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    User save(User user);

    void delete(User user);
}
