---
name: lgpd-compliance
description: >
  Skill de conformidade com a LGPD (Lei Geral de Proteção de Dados) para o PetLife.
  Ativa quando o agente precisa implementar exclusão de dados, consentimento, auditoria,
  portabilidade, soft-delete, ou qualquer funcionalidade relacionada à privacidade e
  segurança de dados pessoais.
  Palavras-chave: LGPD, privacidade, consentimento, exclusão de conta, portabilidade, DPO,
  soft-delete, cascade delete, auditoria, segurança, OWASP, Spring Security.
---

# Skill: LGPD Compliance — PetLife (Spring Boot)

## Artigos LGPD Aplicáveis

| Art. | Requisito                         | Implementação no PetLife                               |
|------|-----------------------------------|--------------------------------------------------------|
| 7º I | Consentimento explícito           | Campo `lgpdAcceptedAt` em `User` + checkbox obrigatório |
| 8º §2| Registro de consentimento         | `lgpd_accepted_at TIMESTAMPTZ` persistido no banco      |
| 9º   | Política de privacidade acessível | Link em todas as telas de auth                          |
| 18 II| Acesso aos dados pelo titular     | `GET /api/v1/users/me/export` — JSON completo           |
| 18 III| Correção de dados               | `PUT /api/v1/users/me` — atualização de perfil          |
| 18 V | Portabilidade                     | Exportação JSON + PDF                                   |
| 18 VI| Eliminação                        | `DELETE /api/v1/auth/account` — cascade rigoroso        |
| 41   | DPO nomeado                       | Contato na política de privacidade                      |
| 48   | Notificação de incidentes         | Processo documentado + PagerDuty                        |

---

## Implementação: Exclusão de Conta (Art. 18 VI)

```java
// AuthService.java
@Transactional
public void deleteAccount(UUID userId, String confirmationPassword) {
    var user = userRepository.findById(userId)
        .orElseThrow(() -> BusinessException.notFound("USER_NOT_FOUND", "Usuário não encontrado"));

    // 1. Verificar senha de confirmação
    if (!passwordEncoder.matches(confirmationPassword, user.getPasswordHash())) {
        throw BusinessException.forbidden("AUTH_INVALID_CREDENTIALS", "Senha incorreta");
    }

    // 2. Excluir todos os arquivos do S3 (fotos, comprovantes, documentos)
    fileService.deleteAllUserFiles(userId);

    // 3. Revogar todos os refresh tokens
    refreshTokenRepository.deleteAllByUserId(userId);

    // 4. Invalidar cache Redis
    redisTemplate.delete(redisTemplate.keys("user:" + userId + ":*"));

    // 5. Excluir usuário — cascade automático via JPA/FK:
    //    users → pets → vaccinations, consultations, medications,
    //             groomings, photos, weight_logs, notifications
    userRepository.delete(user);

    // 6. Registrar no log de auditoria
    auditLogService.record(AuditAction.ACCOUNT_DELETED, userId, "LGPD Art. 18 VI");
}
```

### Endpoint

```java
// DELETE /api/v1/auth/account
@DeleteMapping("/account")
@ResponseStatus(HttpStatus.NO_CONTENT)
@Operation(summary = "Excluir conta e todos os dados (LGPD Art. 18 VI)")
public void deleteAccount(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody DeleteAccountRequest request) {
    authService.deleteAccount(principal.getUserId(), request.password());
}

// DeleteAccountRequest.java
public record DeleteAccountRequest(
    @NotBlank String password,
    @NotBlank @Pattern(regexp = "EXCLUIR MINHA CONTA") String confirmation
) {}
```

---

## Implementação: Soft-Delete (Status de Pet)

Para entidades onde a exclusão imediata não é legalmente necessária:

```java
// PetStatus.java
public enum PetStatus {
    ACTIVE,
    ARCHIVED,   // oculto da lista principal, dados mantidos, restaurável
    DECEASED    // registro histórico preservado
}

// PetService.java
public void archivePet(UUID petId, UUID userId) {
    var pet = getPetOrThrow(petId, userId);
    pet.setStatus(PetStatus.ARCHIVED);
    petRepository.save(pet);
}

// Exclusão física — apenas por solicitação explícita ou ao deletar conta (cascade)
@Transactional
public void deletePet(UUID petId, UUID userId) {
    var pet = getPetOrThrow(petId, userId);
    fileService.deletePetFiles(petId);   // limpar S3
    petRepository.delete(pet);           // cascade JPA remove registros filhos
}
```

---

## Implementação: Exportação de Dados (Art. 18 V)

```java
// UserExportService.java
@Service
@RequiredArgsConstructor
public class UserExportService {

    public UserExportDto exportUserData(UUID userId) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> BusinessException.notFound("USER_NOT_FOUND", "Usuário não encontrado"));

        // NUNCA incluir passwordHash na exportação
        var sanitizedUser = new UserExportDto.UserData(
            user.getId(), user.getName(), user.getEmail(),
            user.getTimezone(), user.getPlan(), user.getCreatedAt()
            // sem passwordHash!
        );

        var pets = petRepository.findAllByUserId(userId);
        var vaccinations = vaccinationRepository.findAllByPetUserIdWithPetName(userId);
        var consultations = consultationRepository.findAllByPetUserId(userId);
        var medications = medicationRepository.findAllByPetUserId(userId);

        return new UserExportDto(
            Instant.now().toString(),
            sanitizedUser,
            pets,
            vaccinations,
            consultations,
            medications
        );
    }
}

// GET /api/v1/users/me/export
@GetMapping("/me/export")
@Operation(summary = "Exportar todos os dados do usuário (LGPD Art. 18 V)")
public ResponseEntity<UserExportDto> exportData(@AuthenticationPrincipal UserPrincipal principal) {
    var export = userExportService.exportUserData(principal.getUserId());
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"petlife-export.json\"")
        .body(export);
}
```

---

## Auditoria de Operações de Escrita

```java
// AuditLogService.java
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    public void record(AuditAction action, UUID actorId, String context) {
        // Log estruturado JSON (nunca logar dados sensíveis)
        log.info("audit=true action={} actor_id={} context=\"{}\" timestamp={}",
            action, actorId, context, Instant.now());
    }
}

// AuditAspect.java — AOP para interceptar todas as operações de escrita
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogService auditLogService;

    @AfterReturning("@annotation(Auditable)")
    public void audit(JoinPoint joinPoint) {
        // extrai userId do SecurityContext
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken token) {
            auditLogService.record(
                AuditAction.from(joinPoint.getSignature().getName()),
                token.getUserId(),
                joinPoint.getSignature().toShortString()
            );
        }
    }
}
```

---

## Segurança — OWASP Top 10

| Vulnerabilidade          | Proteção Implementada                                            |
|--------------------------|------------------------------------------------------------------|
| SQL Injection            | Spring Data JPA com queries parametrizadas (JPQL / Criteria API) |
| Auth Quebrada            | JWT RS256, refresh tokens rotativos com revogação no Redis       |
| Broken Object Auth (IDOR)| Verificar `userId` em **todos** os recursos acessados           |
| XSS                      | Jackson escapa HTML por padrão, Content-Security-Policy header  |
| Security Misconfiguration| Spring Security com configuração explícita, sem defaults inseguros |
| Rate Limiting            | Bucket4j: 100 req/min/usuário, 5 logins/5min por IP            |
| Sensitive Data Exposure  | `passwordHash` jamais serializado em respostas JSON             |
| CSRF                     | Stateless JWT — CSRF não aplicável; `SameSite=Strict` em cookies |

### Proteção Anti-IDOR (Obrigatória em Todo CRUD)

```java
// PetService.java — SEMPRE verificar que o pet pertence ao usuário
public Pet getPetOrThrow(UUID petId, UUID userId) {
    return petRepository.findByIdAndUserId(petId, userId)  // ← OBRIGATÓRIO
        .orElseThrow(() -> BusinessException.notFound("PET_NOT_FOUND", "Pet não encontrado"));
}

// PetRepository.java
public interface PetRepository extends JpaRepository<Pet, UUID> {
    Optional<Pet> findByIdAndUserId(UUID id, UUID userId);  // seguro contra IDOR
    Optional<Pet> findByIdAndUserIdAndStatusNot(UUID id, UUID userId, PetStatus status);
    long countByUserIdAndStatusNot(UUID userId, PetStatus status);
}
```

---

## Testes de Conformidade LGPD (TDD)

```java
// LgpdComplianceTest.java
@ExtendWith(MockitoExtension.class)
class LgpdComplianceTest {

    @Test
    @DisplayName("deleteAccount deve remover usuário e acionar cascade nos pets")
    void deleteAccountShouldRemoveUserAndCascadePets() {
        var user = UserFactory.make();
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(any(), any())).willReturn(true);

        authService.deleteAccount(user.getId(), "Senha@123");

        then(userRepository).should().delete(user);
        // cascade JPA cobre pets → vaccinations, etc.
    }

    @Test
    @DisplayName("exportUserData NÃO deve incluir passwordHash")
    void exportShouldNeverIncludePasswordHash() {
        var user = UserFactory.make();
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        var result = userExportService.exportUserData(user.getId());

        assertThat(result.user().passwordHash()).isNull();
    }

    @Test
    @DisplayName("getPetOrThrow deve lançar 404 se pet pertence a outro usuário (anti-IDOR)")
    void shouldThrowWhenPetBelongsToAnotherUser() {
        var userId = UUID.randomUUID();
        var otherUserId = UUID.randomUUID();
        given(petRepository.findByIdAndUserId(any(), eq(userId))).willReturn(Optional.empty());

        assertThatThrownBy(() -> petService.getPetOrThrow(UUID.randomUUID(), userId))
            .isInstanceOf(BusinessException.class)
            .extracting("code").isEqualTo("PET_NOT_FOUND");
    }
}
```

---

## Checklist de Conformidade por Feature

Ao implementar qualquer funcionalidade com dados pessoais:

- [ ] Consentimento registrado com timestamp?
- [ ] Cascade delete cobre todos os dados ao deletar usuário/pet?
- [ ] `passwordHash` jamais exposto em resposta ou log?
- [ ] IDOR protegido com `findByIdAndUserId`?
- [ ] Operação registrada no log de auditoria?
- [ ] Rate limiting configurado no endpoint?
- [ ] Exportação de dados cobre o novo dado adicionado?
