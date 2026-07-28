# Workflow: Implementar Endpoint Faltante

## Quando Usar
Use este workflow sempre que for implementar um endpoint listado como ausente
em `docs/compliance-report.md` ou no Mapa de Débitos do `GEMINI.md`.

## Pré-requisitos
- [ ] Ler `GEMINI.md` seção 12 (Regras de Ouro)
- [ ] Ler `docs/DOD.md` (Definition of Done)
- [ ] Identificar o débito no `GEMINI.md` seção 6

## Passos

### Fase 1: Research (5–10 min)
```bash
# 1. Identificar o gap
cat docs/compliance-report.md | grep -A5 "❌"

# 2. Ler a spec do PRD para o módulo
# Ex: para M04 (Consultas): ler seção 7.4 do PRD.md

# 3. Verificar se Port precisa de novo método
cat apps/backend/src/main/java/com/petlife/modules/{modulo}/domain/port/*.java
```

### Fase 2: Backend — Implementação
**Ordem obrigatória de criação:**
1. Migration Flyway (se novo campo/tabela)
2. Atualizar JPA Entity (se necessário)
3. Atualizar Mapper (domínio ↔ JPA)
4. Adicionar método ao Port (interface)
5. Implementar no Adapter
6. Implementar Use Case
7. Criar/atualizar DTOs (Request/Response)
8. Criar/atualizar Controller

**Template de branch:**
```bash
git checkout -b feat/m0X-db-{ID}-{descricao-curta}
# Ex: git checkout -b feat/m02-db-03-delete-pet
```

### Fase 3: Gate Check — Obrigatório
```bash
cd apps/backend

# 1. Compilação
mvn clean compile

# 2. Verificações arquiteturais (devem retornar vazio)
grep -rn "ApiResponse" src/main/java/com/petlife/modules/*/application/
grep -rn "JpaRepository\|JpaEntity" src/main/java/com/petlife/modules/*/application/
grep -rL "^package " src/main/java/ --include="*.java"

# 3. Testes
mvn test
```

### Fase 4: Frontend — Atualização
```bash
cd apps/web

# 1. Atualizar HTTP Adapter
# infrastructure/http/{modulo}.api.ts — adicionar novo método

# 2. Atualizar Hook
# application/{modulo}/use{Funcionalidade}.ts

# 3. Atualizar UI (se necessário)
# components/organisms/{Form ou Lista} — adicionar botão/funcionalidade

# Verificações
pnpm lint
pnpm typecheck
pnpm test
```

### Fase 5: Documentação
```bash
# Atualizar compliance-report.md
# Mudar: | `DELETE /pets/:id` | — | ❌ Não implementado |
# Para: | `DELETE /pets/:id` | `DELETE /api/v1/pets/{id}` | ✅ |

# Atualizar porcentagem de cobertura do módulo
```

### Fase 6: Commit e PR
```bash
git add .
git commit -m "feat(m0X): implementa DELETE /{recurso} — fecha DB-{ID}"
git push origin feat/m0X-db-{ID}-{descricao-curta}
```

## Exemplos de Endpoints Críticos

### DB-03: DELETE /api/v1/pets/{id}
```
PRD: seção 7.2.4
Módulo: pet
Impact: LGPD Art. 18 VI
Backend files:
  - DeletePetUseCase.java
  - PetController.java (adicionar endpoint)
  - Verificar cascade no banco
Skill: lgpd-compliance
```

### DB-01: POST /api/v1/auth/refresh
```
PRD: seção 7.1.4 (JWT 24h com refresh 30 dias)
Módulo: auth
Backend files:
  - RefreshTokenUseCase.java
  - AuthController.java (adicionar endpoint)
  - TokenPort.java (adicionar método validateRefreshToken)
Skill: hexagonal-backend
```

### DB-04: GET /api/v1/veterinarians/favorites
```
PRD: seção 7.9.6 (M09-F14)
Módulo: veterinarian
Backend files:
  - GetVetFavoritesUseCase.java
  - VeterinarianController.java (adicionar endpoint)
  - VetFavoriteRepositoryPort.java (adicionar findByUserId)
  - VetFavoritePersistenceAdapter.java (implementar)
Skill: hexagonal-backend
```

## Referências
- `GEMINI.md` seção 6 — mapa de débitos
- `docs/compliance-report.md` — status atual
- `docs/PRD.md` — especificação
- `.gemini/skills/hexagonal-backend/SKILL.md`
- `.gemini/skills/lgpd-compliance/SKILL.md`
