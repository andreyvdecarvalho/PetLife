---
name: api-design
description: >
  Skill de design e implementação de APIs REST para o PetLife com Spring Boot 3.
  Ativa quando o agente precisa criar controllers Spring MVC, definir contratos de
  request/response com DTOs, implementar validação com Bean Validation, documentar
  endpoints com SpringDoc OpenAPI ou revisar padrões de API.
  Palavras-chave: API, REST, endpoint, controller, Spring MVC, Bean Validation, OpenAPI, request, response, validação, DTO.
---

# Skill: API Design — PetLife (Spring Boot 3)

## Padrões de API

### Convenções Gerais
- Prefixo de versionamento: `/api/v1/`
- Formato de resposta: JSON
- Autenticação: Bearer JWT em todos os endpoints (exceto `/api/v1/auth/**`)
- Rate limiting: via **Bucket4j** — 100 req/min por usuário autenticado

### Formato de Resposta Padrão

```java
// ApiResponse.java — wrapper genérico de sucesso
public record ApiResponse<T>(T data, PageMeta meta) {
    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(data, null);
    }
    public static <T> ApiResponse<T> paged(T data, PageMeta meta) {
        return new ApiResponse<>(data, meta);
    }
}

// ErrorResponse.java — wrapper de erro
public record ErrorResponse(ErrorDetail error) {
    public record ErrorDetail(String code, String message, List<FieldError> details) {}
    public record FieldError(String field, String message) {}
}
```

**Sucesso:**
```json
{
  "data": { "id": "uuid", "name": "Luna" },
  "meta": null
}
```

**Lista paginada:**
```json
{
  "data": [...],
  "meta": { "page": 1, "perPage": 20, "total": 100, "totalPages": 5 }
}
```

**Erro:**
```json
{
  "error": {
    "code": "PET_NOT_FOUND",
    "message": "Pet não encontrado",
    "details": []
  }
}
```

---

## Status HTTP por Situação

| Situação                        | Status |
|---------------------------------|--------|
| Criação bem-sucedida            | 201    |
| Operação sem body               | 204    |
| Não autenticado                 | 401    |
| Sem permissão                   | 403    |
| Recurso não encontrado          | 404    |
| Conflito (duplicata)            | 409    |
| Erro de validação (Bean Validation) | 422 |
| Rate limit atingido             | 429    |
| Erro interno                    | 500    |

---

## Estrutura de Controller

```java
// PetController.java
@RestController
@RequestMapping("/api/v1/pets")
@RequiredArgsConstructor
@Tag(name = "Pets", description = "Gestão de pets do usuário")
public class PetController {

    private final PetService petService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar novo pet")
    public ApiResponse<PetResponse> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreatePetRequest request) {
        var pet = petService.createPet(principal.getUserId(), request);
        return ApiResponse.of(PetMapper.toResponse(pet));
    }

    @GetMapping
    @Operation(summary = "Listar pets do usuário")
    public ApiResponse<List<PetResponse>> list(
            @AuthenticationPrincipal UserPrincipal principal) {
        var pets = petService.listPets(principal.getUserId());
        return ApiResponse.of(PetMapper.toResponseList(pets));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhe do pet")
    public ApiResponse<PetResponse> getById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {
        var pet = petService.getPetOrThrow(id, principal.getUserId());
        return ApiResponse.of(PetMapper.toResponse(pet));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pet")
    public ApiResponse<PetResponse> update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePetRequest request) {
        var pet = petService.updatePet(id, principal.getUserId(), request);
        return ApiResponse.of(PetMapper.toResponse(pet));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir pet (cascade)")
    public void delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {
        petService.deletePet(id, principal.getUserId());
    }
}
```

---

## DTOs com Bean Validation

```java
// CreatePetRequest.java
public record CreatePetRequest(
    @NotBlank @Size(min = 2, max = 100)
    String name,

    @NotNull
    PetSpecies species,

    @Size(max = 100)
    String breed,

    @NotNull
    PetSex sex,

    @PastOrPresent
    LocalDate birthDate,

    @DecimalMin("0.01") @DecimalMax("200.00")
    BigDecimal weightKg,

    PetSize size,

    Boolean neutered,

    @Size(max = 50)
    String microchipId,

    String allergies,

    @Size(max = 2000)
    String notes
) {}

// PetResponse.java
public record PetResponse(
    UUID id,
    String name,
    PetSpecies species,
    String breed,
    PetSex sex,
    LocalDate birthDate,
    BigDecimal weightKg,
    PetSize size,
    boolean neutered,
    String microchipId,
    String allergies,
    String notes,
    String photoUrl,
    PetStatus status,
    LocalDateTime createdAt
) {}
```

---

## Handler Global de Erros

```java
// GlobalExceptionHandler.java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        log.warn("Business rule violation: {}", ex.getCode());
        return ResponseEntity
            .status(ex.getStatus())
            .body(new ErrorResponse(new ErrorDetail(ex.getCode(), ex.getMessage(), List.of())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        var details = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> new FieldError(e.getField(), e.getDefaultMessage()))
            .toList();
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(new ErrorResponse(new ErrorDetail("VALIDATION_ERROR", "Dados inválidos", details)));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse(new ErrorDetail("FORBIDDEN", "Acesso negado", List.of())));
    }
}

// BusinessException.java
public class BusinessException extends RuntimeException {
    private final String code;
    private final HttpStatus status;

    public BusinessException(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    // Factory methods padronizados
    public static BusinessException notFound(String code, String msg) {
        return new BusinessException(code, msg, HttpStatus.NOT_FOUND);
    }
    public static BusinessException conflict(String code, String msg) {
        return new BusinessException(code, msg, HttpStatus.CONFLICT);
    }
    public static BusinessException forbidden(String code, String msg) {
        return new BusinessException(code, msg, HttpStatus.FORBIDDEN);
    }
}
```

---

## Códigos de Erro Padronizados (PetLife)

| Código                       | Módulo | Status |
|------------------------------|--------|--------|
| `AUTH_INVALID_CREDENTIALS`   | M01    | 401    |
| `AUTH_EMAIL_ALREADY_EXISTS`  | M01    | 409    |
| `AUTH_EMAIL_NOT_VERIFIED`    | M01    | 403    |
| `AUTH_TOKEN_EXPIRED`         | M01    | 401    |
| `PET_NOT_FOUND`              | M02    | 404    |
| `PET_LIMIT_REACHED`          | M02    | 422    |
| `PET_ARCHIVED`               | M02    | 422    |
| `VACCINATION_NOT_FOUND`      | M03    | 404    |
| `CONSULTATION_NOT_FOUND`     | M04    | 404    |
| `MEDICATION_NOT_FOUND`       | M05    | 404    |
| `FILE_TOO_LARGE`             | Global | 422    |
| `FILE_TYPE_NOT_ALLOWED`      | Global | 422    |
| `RATE_LIMIT_EXCEEDED`        | Global | 429    |
| `VALIDATION_ERROR`           | Global | 422    |

---

## Autenticação JWT com Spring Security

```java
// SecurityConfig.java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**", "/actuator/health", "/docs/**", "/v3/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

---

## Paginação Padrão

```java
// Parâmetros de paginação via Pageable
@GetMapping
public ApiResponse<List<VaccinationResponse>> list(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable UUID petId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {

    var pageable = PageRequest.of(page, Math.min(size, 100), Sort.by("dateAdministered").descending());
    var pageResult = vaccinationService.listByPet(petId, principal.getUserId(), pageable);

    return ApiResponse.paged(
        pageResult.getContent().stream().map(VaccinationMapper::toResponse).toList(),
        new PageMeta(pageResult.getNumber() + 1, pageResult.getSize(), pageResult.getTotalElements(), pageResult.getTotalPages())
    );
}
```

---

## Documentação OpenAPI (SpringDoc)

```yaml
# application.yml
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /docs
    enabled: ${SWAGGER_ENABLED:false}   # apenas em dev/staging
  info:
    title: PetLife API
    version: v1
    description: API REST do PetLife — gestão de saúde e rotina de pets
```

Todos os controllers DEVEM ter:
- `@Tag(name = "...", description = "...")` na classe
- `@Operation(summary = "...")` em cada método
- `@ApiResponse` para status relevantes (opcional, mas recomendado)
