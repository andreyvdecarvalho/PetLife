---
name: hexagonal-backend
description: Implementar módulos no backend Java seguindo rigorosamente a Arquitetura Hexagonal (Ports & Adapters) do PetLife. Use esta skill ao criar ou modificar qualquer código Java no diretório apps/backend/.
---

# Skill: Hexagonal Backend — PetLife

## Quando Usar Esta Skill
- Criar um novo endpoint de API no backend
- Implementar um caso de uso (Use Case) novo
- Corrigir débitos técnicos de arquitetura
- Adicionar entidades ou tabelas ao banco de dados
- Implementar Ports, Adapters ou Mappers

## Contexto do Projeto
- **Framework:** Spring Boot 4.1.0 + Java 21
- **Arquitetura:** Hexagonal (Ports & Adapters) — ver `docs/SDD-hexagonal-refactoring.md`
- **Banco:** PostgreSQL 16.4 + Flyway 10.20.0
- **Testes:** JUnit 5 + Testcontainers + ArchUnit
- **Documentação:** `docs/PRD.md` (requisitos) | `docs/compliance-report.md` (gaps)

## Estrutura Obrigatória de um Módulo

```
com.petlife.modules.{modulo}/
├── domain/
│   ├── entity/
│   │   └── {Entidade}.java           <- POJO puro, sem @Entity
│   └── port/
│       └── {Entidade}RepositoryPort.java  <- Interface pura
├── application/
│   └── usecase/
│       ├── Create{Entidade}UseCase.java
│       ├── Get{Entidade}UseCase.java
│       ├── Update{Entidade}UseCase.java
│       └── Delete{Entidade}UseCase.java
└── infrastructure/
    ├── controller/
    │   └── {Entidade}Controller.java
    ├── persistence/
    │   ├── entity/
    │   │   └── {Entidade}JpaEntity.java  <- @Entity aqui
    │   ├── mapper/
    │   │   └── {Entidade}Mapper.java
    │   ├── repository/
    │   │   └── {Entidade}JpaRepository.java  <- extends JpaRepository<{Entidade}JpaEntity, UUID>
    │   └── adapter/
    │       └── {Entidade}PersistenceAdapter.java  <- implements {Entidade}RepositoryPort
    └── dto/
        ├── Create{Entidade}Request.java
        ├── Update{Entidade}Request.java
        └── {Entidade}Response.java
```

## Templates de Código

### 1. Entidade de Domínio (POJO puro)
```java
package com.petlife.modules.{modulo}.domain.entity;

import java.util.UUID;
import java.time.LocalDateTime;

// SEM @Entity, SEM @Table, SEM imports jakarta.persistence
public class {Entidade} {
    private UUID id;
    private UUID petId;
    private String name;
    private LocalDateTime createdAt;
    
    // Getters e setters / Builder pattern
    // Lombok @Data/@Builder é permitido
}
```

### 2. Port de Repositório (Interface pura)
```java
package com.petlife.modules.{modulo}.domain.port;

import com.petlife.modules.{modulo}.domain.entity.{Entidade};
import com.petlife.shared.PagedResult;
import java.util.Optional;
import java.util.UUID;

// SEM imports Page<>, Pageable, JpaRepository
public interface {Entidade}RepositoryPort {
    {Entidade} save({Entidade} entity);
    Optional<{Entidade}> findById(UUID id);
    PagedResult<{Entidade}> findByPetId(UUID petId, int page, int size);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
```

### 3. JPA Entity
```java
package com.petlife.modules.{modulo}.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "{tabela}")
@Data
public class {Entidade}JpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "pet_id", nullable = false)
    private UUID petId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

### 4. Mapper
```java
package com.petlife.modules.{modulo}.infrastructure.persistence.mapper;

import com.petlife.modules.{modulo}.domain.entity.{Entidade};
import com.petlife.modules.{modulo}.infrastructure.persistence.entity.{Entidade}JpaEntity;
import org.springframework.stereotype.Component;

@Component
public class {Entidade}Mapper {
    
    public {Entidade}JpaEntity toJpa({Entidade} domain) {
        if (domain == null) return null;
        {Entidade}JpaEntity jpa = new {Entidade}JpaEntity();
        jpa.setId(domain.getId());
        jpa.setPetId(domain.getPetId());
        jpa.setName(domain.getName());
        return jpa;
    }
    
    public {Entidade} toDomain({Entidade}JpaEntity jpa) {
        if (jpa == null) return null;
        {Entidade} domain = new {Entidade}();
        domain.setId(jpa.getId());
        domain.setPetId(jpa.getPetId());
        domain.setName(jpa.getName());
        domain.setCreatedAt(jpa.getCreatedAt());
        return domain;
    }
}
```

### 5. JPA Repository
```java
package com.petlife.modules.{modulo}.infrastructure.persistence.repository;

import com.petlife.modules.{modulo}.infrastructure.persistence.entity.{Entidade}JpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

// Parametrizado com JpaEntity, NUNCA com entidade de domínio
public interface {Entidade}JpaRepository extends JpaRepository<{Entidade}JpaEntity, UUID> {
    Page<{Entidade}JpaEntity> findByPetId(UUID petId, Pageable pageable);
}
```

### 6. Persistence Adapter
```java
package com.petlife.modules.{modulo}.infrastructure.persistence.adapter;

import com.petlife.modules.{modulo}.domain.entity.{Entidade};
import com.petlife.modules.{modulo}.domain.port.{Entidade}RepositoryPort;
import com.petlife.modules.{modulo}.infrastructure.persistence.mapper.{Entidade}Mapper;
import com.petlife.modules.{modulo}.infrastructure.persistence.repository.{Entidade}JpaRepository;
import com.petlife.shared.PagedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class {Entidade}PersistenceAdapter implements {Entidade}RepositoryPort {
    
    private final {Entidade}JpaRepository jpaRepository;
    private final {Entidade}Mapper mapper;
    
    @Override
    public {Entidade} save({Entidade} entity) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpa(entity)));
    }
    
    @Override
    public Optional<{Entidade}> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
    
    @Override
    public PagedResult<{Entidade}> findByPetId(UUID petId, int page, int size) {
        Page<{Entidade}JpaEntity> jpaPage = jpaRepository.findByPetId(petId, PageRequest.of(page, size));
        return new PagedResult<>(
            jpaPage.getContent().stream().map(mapper::toDomain).toList(),
            jpaPage.getTotalElements(),
            jpaPage.getTotalPages(),
            jpaPage.getNumber()
        );
    }
    
    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
}
```

### 7. Use Case
```java
package com.petlife.modules.{modulo}.application.usecase;

import com.petlife.modules.{modulo}.domain.entity.{Entidade};
import com.petlife.modules.{modulo}.domain.port.{Entidade}RepositoryPort;
import com.petlife.shared.PagedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.UUID;

// SEM ApiResponse, SEM Page<>, SEM Pageable, SEM JpaRepository
@Component
@RequiredArgsConstructor
public class Get{Entidade}sUseCase {
    
    private final {Entidade}RepositoryPort repository;
    
    public PagedResult<{Entidade}> execute(UUID petId, int page, int size) {
        return repository.findByPetId(petId, page, size);
    }
}
```

### 8. Controller
```java
package com.petlife.modules.{modulo}.infrastructure.controller;

import com.petlife.modules.{modulo}.application.usecase.Get{Entidade}sUseCase;
import com.petlife.modules.{modulo}.infrastructure.dto.{Entidade}Response;
import com.petlife.shared.ApiResponse;
import com.petlife.shared.PagedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pets/{petId}/{recursos}")
@RequiredArgsConstructor
public class {Entidade}Controller {
    
    private final Get{Entidade}sUseCase get{Entidade}sUseCase;
    
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResult<{Entidade}Response>>> list(
            @PathVariable UUID petId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        // Controller constrói ApiResponse a partir do resultado do Use Case
        PagedResult<...> result = get{Entidade}sUseCase.execute(petId, page, size);
        return ResponseEntity.ok(ApiResponse.paged(result));
    }
}
```

## Gate Check — Executar antes de marcar como concluído

```bash
cd apps/backend

# 1. Compilação limpa
mvn clean compile

# 2. ApiResponse em Use Cases (deve retornar vazio)
grep -rn "ApiResponse" src/main/java/com/petlife/modules/*/application/

# 3. JpaRepository ou JpaEntity em application (deve retornar vazio)
grep -rn "JpaRepository\|JpaEntity" src/main/java/com/petlife/modules/*/application/

# 4. Arquivos Java sem package (deve retornar vazio)
grep -rL "^package " src/main/java/ --include="*.java"

# 5. Testes
mvn test
```

## Regras de Ouro (Do NOT Violate)

1. **NUNCA** coloque `@Entity`, `@Table`, `jakarta.persistence.*` em `domain/entity/`
2. **NUNCA** use `Page<>` ou `Pageable` em Ports ou Use Cases
3. **NUNCA** retorne `ApiResponse<>` em Use Cases
4. **SEMPRE** crie o Mapper ANTES do Adapter
5. **SEMPRE** busque todos os call sites ao renomear métodos de Port
6. **SEMPRE** escreva testes — JaCoCo mínimo 85%
7. Migration Flyway SEMPRE para qualquer alteração no banco

## Referências Internas
- `docs/SDD-hexagonal-refactoring.md` — regras preventivas (seção 4)
- `docs/DOD.md` — Definition of Done
- `docs/compliance-report.md` — gaps atuais
- `docs/PRD.md` — especificação de requisitos
