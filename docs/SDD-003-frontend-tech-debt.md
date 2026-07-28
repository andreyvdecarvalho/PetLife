# Spec Driven Development (SDD-003): Débitos Técnicos Frontend

> **Status:** 🟡 Pendente | **Prioridade:** P2 — Débitos Técnicos e Arquitetura
> **Responsável:** Agente Implementador | **Sprint:** 4 (Semana 4)
> **Débitos cobertos:** DT-04, DT-05, DT-06, DT-07, DT-10

## Objetivo
Resolver os débitos técnicos acumulados no frontend para garantir a manutenibilidade, conformidade com os princípios da Clean Architecture (DIP), e remover estilos em linha não autorizados pelo DOD:
- **DT-04** Centralizar o enum `PetSpecies` que está duplicado em vários arquivos de forma incompatível.
- **DT-05** Mover estilos inline de `ProfilePage.tsx` para um arquivo CSS próprio consumindo os tokens do `theme.css`.
- **DT-06** Limpar código visual e lógica inline das páginas de favoritos e perfil de veterinário.
- **DT-07** Mover `WeightRecordResponse` de `infrastructure/dto` para o domínio (DIP).
- **DT-10** Unificar consistência dos caminhos OAuth Google no frontend e backend.

---

## 1. FASE 1 — DT-04: Centralizar `PetSpecies` (2h)

### Contexto
Atualmente, o enum `PetSpecies` está declarado em mais de um módulo no frontend. Isso dificulta integrações e causa erros de tipagem no TypeScript.

### 1.1 Solução

#### [NEW] `apps/web/src/domain/shared/Species.ts`
```typescript
export type PetSpecies = 'dog' | 'cat' | 'bird' | 'fish' | 'rodent' | 'reptile' | 'other';

export const SPECIES_LABELS: Record<PetSpecies, string> = {
  dog: 'Cachorro',
  cat: 'Gato',
  bird: 'Pássaro',
  fish: 'Peixe',
  rodent: 'Roedor',
  reptile: 'Réptil',
  other: 'Outro'
};
```

#### [MODIFY] Alterar referências
Buscar por todas as definições antigas usando `grep -rn "type PetSpecies" apps/web/src/` ou `grep -rn "enum PetSpecies" apps/web/src/` e deletá-las, importando o tipo a partir de `domain/shared/Species`.

---

## 2. FASE 2 — DT-05: Estilos em `ProfilePage` (1h)

### Contexto
O `ProfilePage.tsx` tem estilos inline estruturais e de cores, violando o DOD (regrade Vanilla CSS + tokens obrigatórios).

### 2.1 Solução

#### [NEW] `apps/web/src/components/pages/ProfilePage/styles.css`
```css
.profile-container {
  max-width: var(--max-width);
  margin: 0 auto;
  padding: var(--space-md);
  font-family: var(--font-body);
}

.profile-card {
  background: var(--color-surface);
  border-radius: var(--radius-xl);
  padding: var(--space-md);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--color-outline-variant);
  margin-bottom: var(--space-md);
}

.profile-header {
  font-family: var(--font-headline);
  color: var(--color-primary);
  margin-bottom: var(--space-sm);
}
```

#### [MODIFY] `apps/web/src/pages/ProfilePage.tsx`
Remover estilos inline `style={{ ... }}` e associá-los a classes CSS correspondentes. Importar `./styles.css` no arquivo da página.

---

## 3. FASE 3 — DT-06: Refatorar Lógica Visual Complexa de Páginas (4h)

### Contexto
`VetFavoritesPage` e `VetProfilePage` concentram regras complexas e CSS inline diretamente no arquivo do roteador/página.

### 3.1 Solução

#### [NEW] `apps/web/src/components/organisms/VetFavoritesList/index.tsx`
Mover a lógica de loop de favoritos e estados de carregamento para este organism.

#### [NEW] `apps/web/src/components/organisms/VetFavoritesList/styles.css`
Estilos do container de lista de veterinários favoritos.

#### [MODIFY] `apps/web/src/pages/VetFavoritesPage.tsx`
Simplificar para:
```typescript
import React from 'react';
import { VetFavoritesList } from '../../components/organisms/VetFavoritesList';

export const VetFavoritesPage: React.FC = () => {
  return (
    <div className="page-layout">
      <h1>Meus Veterinários Favoritos</h1>
      <VetFavoritesList />
    </div>
  );
};
```

---

## 4. FASE 4 — DT-07: Inversão de Dependência em `WeightRecord` (1h)

### Contexto
O tipo `WeightRecordResponse` está em `infrastructure/dto` mas está sendo importado diretamente na camada de `application` (hooks), violando o princípio de Clean Architecture.

### 4.1 Solução

#### [NEW] `apps/web/src/domain/pet/WeightRecord.ts`
```typescript
export interface WeightRecord {
  id: string;
  petId: string;
  weightKg: number;
  measuredAt: string;
  source?: string;
  createdAt: string;
}
```

#### [MODIFY] Hooks da camada `application/`
Atualizar imports para consumir `WeightRecord` do domínio, enquanto o adapter HTTP faz a conversão de `WeightRecordResponse` → `WeightRecord` se necessário.

---

## 5. FASE 5 — DT-10: Consistência do path do Google Auth (1h)

### Contexto
Inconsistência nos endpoints de autenticação social do Google.

### 5.1 Solução

#### [MODIFY] `apps/backend/src/main/java/com/petlife/modules/auth/infrastructure/controller/AuthController.java`
Garantir que mapeia `/api/v1/auth/oauth/google` para estar em conformidade estrita com o PRD.

---

## 6. Gate Check — SDD-003
```bash
cd apps/web
pnpm lint
pnpm typecheck
pnpm test
```

---

*SDD-003 criado pela Antigravity AI — 27/07/2026*
