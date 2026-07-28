---
name: api-gap-fixer
description: Identificar e implementar endpoints faltantes no PetLife comparando o PRD com o código atual. Use esta skill quando precisar fechar gaps de API documentados no compliance-report.md.
---

# Skill: API Gap Fixer — PetLife

## Quando Usar Esta Skill
- Implementar endpoints listados como ausentes em `docs/compliance-report.md`
- Fechar débitos DB-01 a DB-23 listados no GEMINI.md
- Conectar funcionalidades de frontend que existem mas não têm backend

## Fontes de Verdade
1. `docs/PRD.md` — especificação do requisito
2. `docs/compliance-report.md` — status atual do que está faltando
3. `GEMINI.md` seção 6 — mapa completo de débitos

## Prioridade de Implementação

### CRÍTICOS — Implementar primeiro
| Débito | Endpoint | Arquivo PRD |
|---|---|---|
| DB-01 | `POST /api/v1/auth/refresh` | Seção 7.1.4 |
| DB-02 | Registrar FCM token no frontend | Seção M08 |
| DB-03 | `DELETE /api/v1/pets/{id}` | Seção 7.2.4 |
| DB-04 | `GET /api/v1/veterinarians/favorites` | Seção 7.9.6 |
| DB-05 | Persistir RSA keys fora da memória | ADR-004 |

### MÉDIOS — Segunda onda
| Débito | Endpoint |
|---|---|
| DB-08 | `DELETE /api/v1/pets/{petId}/vaccines/{id}` |
| DB-09 | Conectar autocomplete de vacinas no frontend |
| DB-11 | `PUT /api/v1/pets/{petId}/consultations/{id}` |
| DB-12 | `DELETE /api/v1/pets/{petId}/consultations/{id}` |
| DB-13 | Peso da consulta atualiza pet automaticamente |
| DB-15 | `PUT /api/v1/pets/{petId}/medications/{id}` |
| DB-18 | `DELETE /api/v1/pets/{petId}/groomings/{id}` |
| DB-19 | `GET /PUT /api/v1/veterinarians/me` |
| DB-20 | PUT/DELETE endereços de veterinário |
| DB-21 | PUT/DELETE horários de veterinário |

## Processo de Implementação de Gap

### Passo 1: Diagnosticar
```bash
# Ver estado atual da API
curl http://localhost:8081/v3/api-docs | jq '.paths | keys'

# Verificar compliance-report
cat docs/compliance-report.md | grep "❌"
```

### Passo 2: Ler a spec do PRD
- Localizar a seção do módulo no PRD.md
- Ler critérios de aceitação
- Identificar modelo de dados necessário

### Passo 3: Implementar no Backend
Seguir a Skill `hexagonal-backend`:
1. Verificar se Port precisa de novo método
2. Implementar no Adapter
3. Implementar Use Case
4. Criar/atualizar Controller + DTOs
5. Migration Flyway se necessário

### Passo 4: Implementar no Frontend
Seguir a Skill `atomic-frontend`:
1. Atualizar HTTP Adapter (`infrastructure/http/*.api.ts`)
2. Atualizar ou criar hook em `application/`
3. Atualizar componente/organism
4. Verificar se precisa de novo componente

### Passo 5: Validar
```bash
# Backend
cd apps/backend && mvn clean compile && mvn test

# Frontend
cd apps/web && pnpm lint && pnpm typecheck && pnpm test
```

### Passo 6: Atualizar documentação
Editar `docs/compliance-report.md`:
- Mudar status de `❌` para `✅`
- Atualizar porcentagem de cobertura do módulo

## Templates por Tipo de Gap

### Gap: DELETE simples
```java
// Use Case
@Component
@RequiredArgsConstructor
public class Delete{Entidade}UseCase {
    private final {Entidade}RepositoryPort repository;
    private final PetRepositoryPort petRepository;
    
    public void execute(UUID petId, UUID entityId, UUID userId) {
        // Validar ownership
        Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new EntityNotFoundException("Pet não encontrado"));
        if (!pet.getUserId().equals(userId)) {
            throw new AccessDeniedException("Acesso não autorizado");
        }
        
        // Verificar se existe
        if (!repository.existsById(entityId)) {
            throw new EntityNotFoundException("{Entidade} não encontrada");
        }
        
        repository.deleteById(entityId);
    }
}

// Controller
@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void delete(
        @PathVariable UUID petId,
        @PathVariable UUID id,
        @AuthenticationPrincipal UserPrincipal user) {
    delete{Entidade}UseCase.execute(petId, id, user.getId());
}
```

### Gap: GET /me para Veterinário
```java
// Use Case
@Component
@RequiredArgsConstructor
public class GetMyVeterinarianProfileUseCase {
    private final VeterinarianRepositoryPort repository;
    
    public Veterinarian execute(UUID userId) {
        return repository.findByUserId(userId)
            .orElseThrow(() -> new EntityNotFoundException("Perfil veterinário não encontrado"));
    }
}

// Controller
@GetMapping("/me")
public ResponseEntity<ApiResponse<VeterinarianResponse>> getMyProfile(
        @AuthenticationPrincipal UserPrincipal user) {
    Veterinarian vet = getMyVeterinarianProfileUseCase.execute(user.getId());
    return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(vet)));
}
```

### Gap: Token Refresh
```java
// Use Case
@Component
@RequiredArgsConstructor
public class RefreshTokenUseCase {
    private final TokenPort tokenPort;
    private final UserRepositoryPort userRepository;
    
    public TokenResponse execute(String refreshToken) {
        // Validar refresh token
        UUID userId = tokenPort.extractUserIdFromRefreshToken(refreshToken);
        
        // Buscar usuário
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));
        
        // Gerar novo access token
        return tokenPort.generateTokens(user);
    }
}
```

### Gap: Frontend — Conectar autocomplete de vacinas
```ts
// vaccination.api.ts — adicionar:
getSuggestions: async (species: string): Promise<string[]> => {
  const { data } = await api.get('/vaccines/suggestions', { params: { species } });
  return data.data;
},

// useVaccineForm.ts — usar as sugestões:
const { data: suggestions } = useQuery({
  queryKey: ['vaccine-suggestions', species],
  queryFn: () => vaccinationApi.getSuggestions(species),
  enabled: !!species,
});
```

## Verificação Final

Antes de marcar um gap como fechado:
1. `mvn clean compile` — sem erros
2. `pnpm lint && pnpm typecheck` — sem erros
3. Teste manual no Swagger: `http://localhost:8081/swagger-ui.html`
4. Editar `docs/compliance-report.md` — atualizar status
5. Commit: `feat(m0X): implementa DELETE {recurso} (fecha DB-XX)`

## Referências
- `docs/compliance-report.md` — estado atual
- `docs/PRD.md` — especificação
- `GEMINI.md` seção 6 — mapa de débitos
- Swagger local: `http://localhost:8081/swagger-ui.html`
