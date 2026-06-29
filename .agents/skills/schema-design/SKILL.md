---
name: schema-design
description: >
  Skill de modelagem de dados e design de schema para o PetLife com Flyway e Spring Data JPA.
  Ativa quando o agente precisa criar ou modificar entidades JPA, escrever migrations Flyway,
  definir relações entre entidades ou validar integridade referencial.
  Palavras-chave: schema, JPA, entity, migration, flyway, model, database, ERD, postgres, relação, FK, cascade, hibernate.
---

# Skill: Schema Design — PetLife (Java / Flyway / JPA)

## Convenções de Entidades JPA

### Regras Gerais
- Todos os `id` são `UUID` gerado com `@GeneratedValue(strategy = GenerationType.UUID)`
- Campos de auditoria: `createdAt` (`@CreationTimestamp`) e `updatedAt` (`@UpdateTimestamp`)
- Soft-delete usa campo `deletedAt LocalDateTime` (quando aplicável)
- Cascade delete obrigatório em `userId` e `petId` (LGPD Art. 18) via `CascadeType.REMOVE` + `orphanRemoval = true`
- Todos os nomes de tabela em `snake_case` via `@Table(name = "...")`
- Todos os nomes de coluna explicitamente declarados via `@Column(name = "...")`

### Entidade Base Obrigatória

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
```

---

## Entidades Principais

### User

```java
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", length = 60)
    private String passwordHash;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "timezone", nullable = false, length = 50)
    private String timezone = "America/Sao_Paulo";

    @Enumerated(EnumType.STRING)
    @Column(name = "plan", nullable = false)
    private UserPlan plan = UserPlan.FREE;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "lgpd_accepted_at")
    private LocalDateTime lgpdAcceptedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Pet> pets = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();
}
```

### Pet

```java
@Entity
@Table(name = "pets", indexes = {
    @Index(name = "idx_pets_user_id", columnList = "user_id"),
    @Index(name = "idx_pets_user_id_status", columnList = "user_id, status")
})
public class Pet extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "species", nullable = false)
    private PetSpecies species;

    @Column(name = "breed", length = 100)
    private String breed;

    @Enumerated(EnumType.STRING)
    @Column(name = "sex", nullable = false)
    private PetSex sex;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "weight_kg", precision = 5, scale = 2)
    private BigDecimal weightKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "size")
    private PetSize size;

    @Column(name = "neutered", nullable = false)
    private boolean neutered = false;

    @Column(name = "microchip_id", length = 50)
    private String microchipId;

    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;

    @Column(name = "notes", length = 2000)
    private String notes;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PetStatus status = PetStatus.ACTIVE;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Vaccination> vaccinations = new ArrayList<>();

    @OneToMany(mappedBy = "pet", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Consultation> consultations = new ArrayList<>();

    @OneToMany(mappedBy = "pet", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Medication> medications = new ArrayList<>();

    @OneToMany(mappedBy = "pet", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Grooming> groomings = new ArrayList<>();
}
```

### Vaccination

```java
@Entity
@Table(name = "vaccinations", indexes = {
    @Index(name = "idx_vaccinations_pet_id", columnList = "pet_id"),
    @Index(name = "idx_vaccinations_next_dose", columnList = "next_dose_date, reminder_active")
})
public class Vaccination extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "vaccine_name", nullable = false, length = 200)
    private String vaccineName;

    @Column(name = "date_administered", nullable = false)
    private LocalDate dateAdministered;

    @Column(name = "next_dose_date")
    private LocalDate nextDoseDate;

    @Column(name = "veterinarian", length = 200)
    private String veterinarian;

    @Column(name = "clinic", length = 200)
    private String clinic;

    @Column(name = "batch_number", length = 50)
    private String batchNumber;

    @Column(name = "manufacturer", length = 200)
    private String manufacturer;

    @Column(name = "proof_url", length = 500)
    private String proofUrl;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "reminder_active", nullable = false)
    private boolean reminderActive = true;
}
```

### Medication + MedicationLog

```java
@Entity
@Table(name = "medications")
public class Medication extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "dosage", nullable = false, length = 100)
    private String dosage;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", nullable = false)
    private MedicationFrequency frequency;

    @Column(name = "custom_frequency_hours")
    private Integer customFrequencyHours;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Type(JsonType.class)
    @Column(name = "times_of_day", columnDefinition = "jsonb", nullable = false)
    private List<String> timesOfDay;

    @Column(name = "prescribed_by", length = 200)
    private String prescribedBy;

    @Column(name = "reason", length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MedicationStatus status = MedicationStatus.ACTIVE;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "medication", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<MedicationLog> logs = new ArrayList<>();
}
```

---

## Migrations Flyway

### Convenção de Nomenclatura

```
src/main/resources/db/migration/
├── V1__create_users_table.sql
├── V2__create_pets_table.sql
├── V3__create_vaccinations_table.sql
├── V4__create_consultations_table.sql
├── V5__create_attachments_table.sql
├── V6__create_medications_table.sql
├── V7__create_medication_logs_table.sql
├── V8__create_groomings_table.sql
├── V9__create_photos_table.sql
├── V10__create_weight_logs_table.sql
├── V11__create_refresh_tokens_table.sql
└── V12__create_notifications_table.sql
```

### Exemplo — V1__create_users_table.sql

```sql
CREATE TYPE user_plan AS ENUM ('FREE', 'PREMIUM', 'FAMILY');

CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(200)  NOT NULL,
    email           VARCHAR(255)  NOT NULL UNIQUE,
    password_hash   VARCHAR(60),
    avatar_url      VARCHAR(500),
    timezone        VARCHAR(50)   NOT NULL DEFAULT 'America/Sao_Paulo',
    plan            user_plan     NOT NULL DEFAULT 'FREE',
    email_verified  BOOLEAN       NOT NULL DEFAULT FALSE,
    lgpd_accepted_at TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users (email);
```

### Exemplo — V2__create_pets_table.sql

```sql
CREATE TYPE pet_species AS ENUM ('DOG', 'CAT', 'BIRD', 'FISH', 'RODENT', 'REPTILE', 'OTHER');
CREATE TYPE pet_sex     AS ENUM ('MALE', 'FEMALE', 'UNKNOWN');
CREATE TYPE pet_size    AS ENUM ('MINI', 'SMALL', 'MEDIUM', 'LARGE', 'GIANT');
CREATE TYPE pet_status  AS ENUM ('ACTIVE', 'ARCHIVED', 'DECEASED');

CREATE TABLE pets (
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name         VARCHAR(100) NOT NULL,
    species      pet_species  NOT NULL,
    breed        VARCHAR(100),
    sex          pet_sex      NOT NULL,
    birth_date   DATE,
    weight_kg    NUMERIC(5,2),
    size         pet_size,
    neutered     BOOLEAN      NOT NULL DEFAULT FALSE,
    microchip_id VARCHAR(50),
    allergies    TEXT,
    notes        VARCHAR(2000),
    photo_url    VARCHAR(500),
    status       pet_status   NOT NULL DEFAULT 'ACTIVE',
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pets_user_id        ON pets (user_id);
CREATE INDEX idx_pets_user_id_status ON pets (user_id, status);
```

---

## Configuração Flyway (`application.yml`)

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    validate-on-migrate: true
  jpa:
    hibernate:
      ddl-auto: validate   # NUNCA usar 'create' ou 'update' fora de dev
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
        jdbc:
          time_zone: UTC
```

---

## Regras de Integridade

1. **Cascade obrigatório**: `users → pets → (vaccinations, consultations, medications, groomings, photos, weight_logs)` — todos com `ON DELETE CASCADE`
2. **Índices**: criar índice em toda FK e campos usados frequentemente em `WHERE`
3. **notifications.pet_id**: usar `ON DELETE SET NULL` (preservar histórico)
4. **UUIDs**: nunca usar `SERIAL`/`INT` como PK — sempre `UUID DEFAULT gen_random_uuid()`
5. **DDL**: sempre via Flyway, **nunca** `ddl-auto: create` em produção
