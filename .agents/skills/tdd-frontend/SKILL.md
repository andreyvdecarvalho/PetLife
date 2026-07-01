---
name: tdd-frontend
description: >
  Skill de desenvolvimento guiado por testes (TDD) para o frontend do PetLife.
  Ativa quando o agente precisa criar, modificar ou revisar componentes React, React Native,
  hooks, stores ou fluxos de UI com foco em testes com Testing Library, Jest e Playwright.
  Palavras-chave: component test, React Testing Library, RTL, Jest, Playwright, hook test,
  snapshot, accessibility, frontend, web, mobile, PWA.
---

# Skill: TDD Frontend — PetLife

## Visão Geral

Guia o desenvolvimento orientado a testes para o frontend do PetLife:
- **Web**: React 19 + Vite (PWA)
- **Mobile**: React Native 0.76+

---

## Stack de Testes Frontend

| Camada             | Ferramenta                           | Escopo                               |
|--------------------|--------------------------------------|--------------------------------------|
| Unit/Component     | **Vitest + React Testing Library**   | Componentes, hooks, utils            |
| Native Components  | **Jest + RNTL**                      | Componentes React Native             |
| Acessibilidade     | **jest-axe / @testing-library/jest-axe** | WCAG 2.1 AA compliance           |
| E2E Web            | **Playwright**                       | Fluxos completos no browser          |
| E2E Mobile         | **Detox**                            | Fluxos completos em emulador/device  |
| Visual Regression  | **Playwright Screenshots**           | Regressão visual de componentes      |
| MSW                | **Mock Service Worker**              | Mock de APIs durante testes          |

---

## Estrutura de Diretórios

```
apps/web/src/
├── components/
│   ├── PetCard/
│   │   ├── PetCard.tsx
│   │   ├── PetCard.spec.tsx          ← component test
│   │   └── PetCard.module.css
├── hooks/
│   ├── usePets.ts
│   └── usePets.spec.ts               ← hook test
├── pages/
│   ├── PetDashboard/
│   │   ├── PetDashboard.tsx
│   │   └── PetDashboard.spec.tsx
└── test/
    ├── setup.ts
    ├── render.tsx                     ← custom render com providers
    ├── msw/
    │   ├── server.ts                  ← MSW node server
    │   └── handlers/                  ← handlers por módulo
    └── factories/                     ← factories de dados

apps/web/e2e/
└── playwright/
    ├── auth.spec.ts
    ├── pet-management.spec.ts
    └── vaccination.spec.ts
```

---

## Ciclo TDD — Componentes React

### Passo 1 — RED

```tsx
// PetCard.spec.tsx
import { screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { renderWithProviders } from '@/test/render'
import { PetCard } from './PetCard'
import { makePet } from '@/test/factories/pet.factory'

describe('PetCard', () => {
  it('should display pet name and species', () => {
    const pet = makePet({ name: 'Luna', species: 'dog' })

    renderWithProviders(<PetCard pet={pet} />)

    expect(screen.getByText('Luna')).toBeInTheDocument()
    expect(screen.getByText(/cachorro/i)).toBeInTheDocument()
  })

  it('should call onSelect when clicked', async () => {
    const pet = makePet()
    const onSelect = vi.fn()
    const user = userEvent.setup()

    renderWithProviders(<PetCard pet={pet} onSelect={onSelect} />)

    await user.click(screen.getByRole('button', { name: /ver detalhes/i }))

    expect(onSelect).toHaveBeenCalledWith(pet.id)
  })

  it('should be accessible (WCAG 2.1 AA)', async () => {
    const pet = makePet()
    const { container } = renderWithProviders(<PetCard pet={pet} />)
    const results = await axe(container)
    expect(results).toHaveNoViolations()
  })
})
```

### Passo 2 — GREEN: implemente o componente
### Passo 3 — REFACTOR: extraia lógica, melhore semântica

---

## Custom Render com Providers

```tsx
// src/test/render.tsx
import { render, RenderOptions } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { BrowserRouter } from 'react-router-dom'
import { ReactNode } from 'react'

function createTestQueryClient() {
  return new QueryClient({
    defaultOptions: {
      queries: { retry: false, gcTime: 0 },
      mutations: { retry: false },
    },
  })
}

export function renderWithProviders(
  ui: ReactNode,
  options?: Omit<RenderOptions, 'wrapper'>
) {
  const queryClient = createTestQueryClient()

  function Wrapper({ children }: { children: ReactNode }) {
    return (
      <QueryClientProvider client={queryClient}>
        <BrowserRouter>{children}</BrowserRouter>
      </QueryClientProvider>
    )
  }

  return render(ui, { wrapper: Wrapper, ...options })
}
```

---

## Mock Service Worker (MSW)

```typescript
// src/test/msw/handlers/pets.handlers.ts
import { http, HttpResponse } from 'msw'
import { makePet } from '@/test/factories/pet.factory'

export const petsHandlers = [
  http.get('/api/v1/pets', () => {
    return HttpResponse.json({
      data: [makePet({ name: 'Luna' }), makePet({ name: 'Simba' })],
    })
  }),

  http.post('/api/v1/pets', async ({ request }) => {
    const body = await request.json()
    return HttpResponse.json(makePet(body as any), { status: 201 })
  }),

  http.get('/api/v1/pets/:id', ({ params }) => {
    return HttpResponse.json(makePet({ id: params.id as string }))
  }),
]
```

---

## Testes de Hooks

```typescript
// usePets.spec.ts
import { renderHook, waitFor } from '@testing-library/react'
import { usePets } from './usePets'
import { createWrapper } from '@/test/render'
import { server } from '@/test/msw/server'
import { http, HttpResponse } from 'msw'

describe('usePets', () => {
  it('should return list of pets', async () => {
    const { result } = renderHook(() => usePets(), {
      wrapper: createWrapper(),
    })

    await waitFor(() => expect(result.current.isSuccess).toBe(true))

    expect(result.current.data).toHaveLength(2)
    expect(result.current.data?.[0].name).toBe('Luna')
  })

  it('should handle API error gracefully', async () => {
    server.use(
      http.get('/api/v1/pets', () =>
        HttpResponse.json({ message: 'Unauthorized' }, { status: 401 })
      )
    )

    const { result } = renderHook(() => usePets(), {
      wrapper: createWrapper(),
    })

    await waitFor(() => expect(result.current.isError).toBe(true))
  })
})
```

---

## Testes E2E com Playwright

```typescript
// e2e/playwright/auth.spec.ts
import { test, expect } from '@playwright/test'

test.describe('Fluxo de Autenticação', () => {
  test('tutor consegue criar conta e cadastrar primeiro pet', async ({ page }) => {
    await page.goto('/cadastro')

    await page.fill('[data-testid="input-name"]', 'Camila Tutora')
    await page.fill('[data-testid="input-email"]', `test+${Date.now()}@petlife.com`)
    await page.fill('[data-testid="input-password"]', 'Senha@123')
    await page.click('[data-testid="btn-aceitar-termos"]')
    await page.click('[data-testid="btn-criar-conta"]')

    await expect(page).toHaveURL('/onboarding/cadastrar-pet')
    await expect(page.getByText('Cadastre seu primeiro pet')).toBeVisible()
  })

  test('login falha com credenciais inválidas', async ({ page }) => {
    await page.goto('/login')

    await page.fill('[data-testid="input-email"]', 'invalido@petlife.com')
    await page.fill('[data-testid="input-password"]', 'senhaerrada')
    await page.click('[data-testid="btn-login"]')

    await expect(page.getByRole('alert')).toContainText(/credenciais inválidas/i)
  })
})
```

---

## Convenções de data-testid

Todo elemento interativo DEVE ter um `data-testid` descritivo:
- Botões: `btn-{ação}` (ex: `btn-salvar-pet`, `btn-registrar-vacina`)
- Inputs: `input-{campo}` (ex: `input-nome-pet`, `input-data-vacina`)
- Cards: `card-pet-{id}`
- Modais: `modal-{nome}`
- Listas: `list-pets`, `list-vaccines`

---

## Acessibilidade — Testes Obrigatórios

```tsx
import { axe } from 'jest-axe'

it('página de cadastro de pet deve estar acessível (WCAG 2.1 AA)', async () => {
  const { container } = renderWithProviders(<CadastroPetPage />)
  const results = await axe(container)
  expect(results).toHaveNoViolations()
})
```

Regras mínimas a testar:
- Contraste de cor ≥ 4.5:1
- Todos os inputs têm `label` associado
- Imagens têm `alt` descritivo
- Navegação por teclado funciona em todos os formulários
- `aria-live` em notificações e alertas

---

## Critérios de Cobertura

| Módulo de UI        | Cobertura Mínima |
|---------------------|------------------|
| Componentes P0      | 85%              |
| Componentes P1      | 75%              |
| Hooks de dados      | 90%              |
| Páginas (E2E)       | Fluxos críticos  |

---

## Regras de Qualidade

1. **Não use `getByTestId` como primeira opção** — prefira queries semânticas (`getByRole`, `getByLabelText`, `getByText`).
2. **Não faça snapshot tests cegos** — snapshots são permitidos apenas para componentes estáticos bem definidos.
3. **Aguarde mudanças assíncronas** — sempre use `waitFor`, `findBy*` ou `await user.click()` corretamente.
4. **Isole efeitos colaterais** — reset o MSW server em `afterEach`.
5. **Testes de acessibilidade são bloqueantes** — `jest-axe` violations devem ser corrigidas antes do merge.
