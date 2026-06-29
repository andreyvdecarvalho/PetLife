---
name: tdd-backend
description: >
  Skill de desenvolvimento guiado por testes (TDD) para o backend do PetLife.
  Ativa quando o agente precisa criar, modificar ou revisar código Java/Spring Boot/JPA
  com foco em testes unitários, de integração e E2E no backend.
  Palavras-chave: test, spec, TDD, unit test, integration test, JUnit, Mockito, Testcontainers,
  backend, Spring Boot, JPA, service, repository.
---

# Skill: TDD Backend — PetLife

## Visão Geral

Este skill guia o desenvolvimento orientado a testes (TDD) para o backend do PetLife (**Java 21 + Spring Boot 3 + Spring Data JPA + PostgreSQL 16**). Siga o ciclo **Red → Green → Refactor** em todas as implementações.

---

## Stack de Testes Backend

| Camada          | Ferramenta                    | Propósito                                                   |
|-----------------|-------------------------------|-------------------------------------------------------------|
| Unit Tests      | **JUnit 5 + Mockito**         | Testar Services e utilitários de forma isolada (sem Spring) |
| Integration     | **@SpringBootTest + MockMvc** | Testar controllers/rotas com contexto Spring                |
| Banco real      | **Testcontainers (PostgreSQL + Redis)** | Testes de integração contra banco real em Docker  |
| Fixtures        | **Java Faker (Datafaker)**    | Geração de dados realistas em PT-BR                         |
| Asserções       | **AssertJ**                   | Asserções fluentes e legíveis                               |
| Coverage        | **JaCoCo**                    | Meta: ≥ 80% de cobertura (lançamento v1.0)                  |
| API Testing     | **MockMvc / RestAssured**     | Testar endpoints HTTP                                       |

---

## Estrutura de Diretórios de Testes

```
src/
├── main/java/com/petlife/
│   └── modules/
│       ├── pet/
│       │   ├── PetController.java
│       │   ├── PetService.java
│       │   ├── PetRepository.java
│       │   ├── entity/Pet.java
│       │   └── dto/
│       └── ...
└── test/java/com/petlife/
    ├── modules/
    │   ├── pet/
    │   │   ├── PetServiceTest.java          ← unit test (sem Spring)
    │   │   └── PetControllerTest.java       ← integration test (@SpringBootTest)
    │   ├── auth/
    │   │   ├── AuthServiceTest.java
    │   │   └── AuthControllerTest.java
    │   └── ...
    └── shared/
        ├── IntegrationTestBase.java         ← base com Testcontainers
        └── factories/
            ├── PetFactory.java
            ├── UserFactory.java
            └── VaccinationFactory.java
```

---

## Ciclo TDD Obrigatório

### Passo 1 — RED: Escreva o teste primeiro

```java
// PetServiceTest.java
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PetService petService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve criar um pet para o usuário autenticado")
    void shouldCreatePetForAuthenticatedUser() {
        // Arrange
        var userId = UUID.randomUUID();
        var request = new CreatePetRequest("Luna", PetSpecies.DOG, PetSex.FEMALE);
        var user = UserFactory.make(u -> u.setId(userId));
        var expectedPet = PetFactory.make(p -> {
            p.setName("Luna");
            p.setUser(user);
        });

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(petRepository.save(any(Pet.class))).willReturn(expectedPet);

        // Act
        var result = petService.createPet(userId, request);

        // Assert
        assertThat(result.getName()).isEqualTo("Luna");
        assertThat(result.getSpecies()).isEqualTo(PetSpecies.DOG);
        then(petRepository).should().save(any(Pet.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário Free tenta cadastrar 3º pet")
    void shouldThrowWhenFreePlanExceedsLimit() {
        var userId = UUID.randomUUID();
        var user = UserFactory.make(u -> u.setPlan(UserPlan.FREE));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(petRepository.countByUserIdAndStatusNot(userId, PetStatus.ARCHIVED)).willReturn(2L);

        assertThatThrownBy(() -> petService.createPet(userId, any()))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("PET_LIMIT_REACHED");
    }
}
```

### Passo 2 — GREEN: Escreva o mínimo de código para passar
### Passo 3 — REFACTOR: Melhore sem quebrar testes

---

## Base de Integração com Testcontainers

```java
// IntegrationTestBase.java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public abstract class IntegrationTestBase {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("petlife_test")
            .withUsername("petlife")
            .withPassword("petlife_test");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
}
```

---

## Factories com Datafaker

```java
// PetFactory.java
public class PetFactory {

    private static final Faker faker = new Faker(new Locale("pt", "BR"));

    public static Pet make() {
        return make(p -> {});
    }

    public static Pet make(Consumer<Pet> overrides) {
        var pet = new Pet();
        pet.setId(UUID.randomUUID());
        pet.setName(faker.dog().name());
        pet.setSpecies(PetSpecies.DOG);
        pet.setSex(PetSex.FEMALE);
        pet.setStatus(PetStatus.ACTIVE);
        pet.setNeutered(false);
        pet.setCreatedAt(LocalDateTime.now());
        pet.setUpdatedAt(LocalDateTime.now());
        overrides.accept(pet);
        return pet;
    }
}
```

---

## Testes de Controller (Integration)

```java
// AuthControllerTest.java
class AuthControllerTest extends IntegrationTestBase {

    @Test
    @DisplayName("POST /api/v1/auth/register - 201 com tokens válidos")
    void shouldRegisterUserAndReturnTokens() throws Exception {
        var request = new RegisterRequest(
            "Camila Tutora",
            "camila+" + System.currentTimeMillis() + "@petlife.com",
            "Senha@123"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.accessToken").isString())
            .andExpect(jsonPath("$.data.refreshToken").isString());
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - 409 quando e-mail já existe")
    void shouldReturn409WhenEmailAlreadyExists() throws Exception {
        // cria o usuário primeiro
        var existingUser = UserFactory.make();
        userRepository.save(existingUser);

        var request = new RegisterRequest("Outro Nome", existingUser.getEmail(), "Senha@123");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error.code").value("AUTH_EMAIL_ALREADY_EXISTS"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - 422 quando senha fraca")
    void shouldReturn422WhenPasswordIsTooWeak() throws Exception {
        var request = new RegisterRequest("Camila", "camila@petlife.com", "fraca");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }
}
```

---

## Convenções de Nomenclatura de Testes

- Classes de unit test: `{NomeClasse}Test.java`
- Classes de integration test: `{NomeClasse}IT.java` ou `{NomeController}Test.java extends IntegrationTestBase`
- Método de teste: `should{Comportamento}When{Condição}()`
- Use `@DisplayName` em PT-BR para documentar o comportamento
- Use `@Nested` para agrupar cenários dentro de um `describe` lógico

```java
@Nested
@DisplayName("createPet")
class CreatePet {
    @Test
    @DisplayName("Deve criar pet com dados válidos")
    void shouldCreatePetWithValidData() { ... }

    @Test
    @DisplayName("Deve lançar exceção se pet não pertence ao usuário")
    void shouldThrowIfPetDoesNotBelongToUser() { ... }
}
```

---

## Critérios de Cobertura por Módulo

| Módulo           | Cobertura Mínima | Tipos de Teste Obrigatórios                   |
|------------------|------------------|-----------------------------------------------|
| M01 — Auth       | 90%              | Unit (Service) + Integration (Controller)     |
| M02 — Pets       | 85%              | Unit + Integration                            |
| M03 — Vacinas    | 85%              | Unit + Integration                            |
| M04 — Consultas  | 80%              | Unit + Integration                            |
| M05 — Medicamentos | 85%            | Unit + Integration (scheduler)                |
| M06 — Banho/Tosa | 75%              | Unit + Integration                            |
| M07 — Timeline   | 75%              | Integration                                   |
| M08 — Notificações | 80%            | Unit (Scheduler) + Integration               |

---

## Configuração do JaCoCo (`pom.xml`)

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.12</version>
    <executions>
        <execution>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals><goal>report</goal></goals>
        </execution>
        <execution>
            <id>check</id>
            <goals><goal>check</goal></goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                            <limit>
                                <counter>BRANCH</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.75</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
                <excludes>
                    <exclude>com/petlife/*Application.class</exclude>
                    <exclude>com/petlife/config/**</exclude>
                    <exclude>com/petlife/**/*Dto.class</exclude>
                    <exclude>com/petlife/**/*Request.class</exclude>
                    <exclude>com/petlife/**/*Response.class</exclude>
                </excludes>
            </configuration>
        </execution>
    </executions>
</plugin>
```

---

## Regras de Qualidade

1. **Nunca faça commit sem testes passando** — o CI bloqueia merges com testes falhando.
2. **Mocks obrigatórios em unit tests** — Firebase, S3, RabbitMQ, Redis DEVEM ser mockados com Mockito.
3. **Testcontainers para integration tests** — banco PostgreSQL real, nunca H2 em memória.
4. **Cada teste deve ser independente** — use `@Transactional` com rollback ou `@Sql` para limpar dados.
5. **Toda regra de negócio fica no Service** — Controllers são finos (apenas recebem, delegam e retornam).
6. **Não teste implementações, teste comportamentos** — verifique o resultado, não como chegou lá.

---

## Referências

- [Spring Boot Testing Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Testcontainers Java](https://java.testcontainers.org/)
- [Mockito Documentation](https://site.mockito.org/)
- [AssertJ](https://assertj.github.io/doc/)
- [JaCoCo Maven Plugin](https://www.jacoco.org/jacoco/trunk/doc/maven.html)
