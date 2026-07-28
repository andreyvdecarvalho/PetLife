---
name: lgpd-compliance
description: Verificar e implementar requisitos de conformidade com a LGPD (Lei Geral de Proteção de Dados) no PetLife. Use esta skill ao implementar exclusão de dados, exportação, consentimento ou qualquer funcionalidade que trate dados pessoais.
---

# Skill: LGPD Compliance — PetLife

## Quando Usar Esta Skill
- Implementar ou revisar exclusão de conta/dados
- Implementar exportação de dados do usuário (portabilidade)
- Adicionar campos de consentimento
- Revisar se cascade delete está correto
- Implementar qualquer funcionalidade que trate dados pessoais

## Base Legal no PRD
- PRD seção 9, RNF-002 — Requisitos de LGPD e Privacidade
- `docs/ADR-005-lgpd-data-compliance.md` — decisão arquitetural
- Referência legal: Lei 13.709/2018 (LGPD)

## Artigos LGPD Relevantes para PetLife

| Artigo | Requisito | Status |
|---|---|---|
| Art. 7º, I | Consentimento explícito no cadastro | Implementado |
| Art. 18, II | Acesso aos dados pessoais pelo titular | Parcial |
| Art. 18, III | Correção de dados incompletos | Parcial |
| Art. 18, V | Portabilidade dos dados (exportação) | Parcial (backend só) |
| Art. 18, VI | Eliminação dos dados (exclusão de conta) | CRÍTICO — pets não excluídos (DB-03) |
| Art. 8º, §2 | Registro de consentimento com timestamp | Implementado |
| Art. 9º | Política de privacidade acessível | Verificar |
| Art. 48 | Notificação de incidentes de segurança | Pendente |
| Art. 41 | Nomeação de DPO | Organizacional |

## Gap Crítico Atual: DB-03

```
DELETE /api/v1/pets/{id} — AUSENTE NO BACKEND
Isso é violação do Art. 18, VI da LGPD (direito à eliminação)
```

### Implementação Correta do DELETE /pets/{id}

```java
// Garante cascade delete de TODOS os dados vinculados
@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void deletePet(
        @PathVariable UUID id,
        @AuthenticationPrincipal UserPrincipal user) {
    deletePetUseCase.execute(id, user.getId());
}
```

```java
// Use Case — com validação de ownership
@Component
@RequiredArgsConstructor
public class DeletePetUseCase {
    private final PetRepositoryPort petRepository;
    
    @Transactional // Adapter garante cascade
    public void execute(UUID petId, UUID userId) {
        Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new EntityNotFoundException("Pet não encontrado"));
        
        if (!pet.getUserId().equals(userId)) {
            throw new AccessDeniedException("Você não tem permissão para excluir este pet");
        }
        
        // Cascade delete deve remover:
        // - vaccinations
        // - consultations (+ attachments)
        // - medications (+ medication_logs)
        // - groomings
        // - photos
        // - weight_logs
        // - timeline events
        petRepository.deleteById(petId);
    }
}
```

```sql
-- Migration Flyway para garantir cascade no banco
-- VX__add_cascade_delete_pet.sql

ALTER TABLE vaccinations
    DROP CONSTRAINT IF EXISTS vaccinations_pet_id_fkey,
    ADD CONSTRAINT vaccinations_pet_id_fkey
        FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE;

ALTER TABLE consultations
    DROP CONSTRAINT IF EXISTS consultations_pet_id_fkey,
    ADD CONSTRAINT consultations_pet_id_fkey
        FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE;

ALTER TABLE medications
    DROP CONSTRAINT IF EXISTS medications_pet_id_fkey,
    ADD CONSTRAINT medications_pet_id_fkey
        FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE;

ALTER TABLE groomings
    DROP CONSTRAINT IF EXISTS groomings_pet_id_fkey,
    ADD CONSTRAINT groomings_pet_id_fkey
        FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE;

ALTER TABLE photos
    DROP CONSTRAINT IF EXISTS photos_pet_id_fkey,
    ADD CONSTRAINT photos_pet_id_fkey
        FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE;

ALTER TABLE weight_logs
    DROP CONSTRAINT IF EXISTS weight_logs_pet_id_fkey,
    ADD CONSTRAINT weight_logs_pet_id_fkey
        FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE;
```

## DELETE /auth/account — Exclusão Completa

```java
// Deve remover em cascata TUDO do usuário:
// - pets (e seus filhos — ver cascade acima)
// - veterinarian profile (se existir)
// - vet_addresses, vet_schedules, vet_favorites
// - notifications
// - device_tokens

@DeleteMapping("/account")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void deleteAccount(@AuthenticationPrincipal UserPrincipal user) {
    deleteAccountUseCase.execute(user.getId());
}
```

## Portabilidade de Dados (Art. 18, V)

O endpoint `GET /api/v1/pets/{petId}/export` existe no backend mas não é acessível no frontend.

**Para implementar no frontend:**
```ts
// pet.api.ts — adicionar:
exportMedicalPass: async (petId: string): Promise<Blob> => {
  const response = await api.get(`/pets/${petId}/export`, {
    responseType: 'blob',
  });
  return response.data;
},

// useExportMedicalPass.ts — hook de aplicação
// Usar BrowserAdapter para criar download (não manipular DOM diretamente no hook)
```

## Consentimento e Registro

Ao implementar qualquer feature que colete dados pessoais:
1. O consentimento deve ser explícito (checkbox, não pré-marcado)
2. Registrar timestamp do aceite: `consent_timestamp TIMESTAMPTZ NOT NULL`
3. Registrar versão da política: `policy_version VARCHAR(10)`
4. Armazenar o IP de origem (hash)

## Checklist LGPD para PRs

- [ ] Exclusão em cascata implementada para novos relacionamentos
- [ ] Foreign keys têm `ON DELETE CASCADE` na migration
- [ ] Dados pessoais criptografados em repouso (AES-256)
- [ ] Logs de auditoria para operações de escrita
- [ ] Nenhum dado pessoal em logs de aplicação
- [ ] Exports incluem todos os dados do titular
- [ ] Exclusão funciona dentro de 72h (SLA LGPD)

## Referências
- `docs/PRD.md` seção 9, RNF-002
- `docs/ADR-005-lgpd-data-compliance.md`
- `docs/compliance-report.md` seção M02 (DELETE /pets)
- Lei 13.709/2018 — LGPD
