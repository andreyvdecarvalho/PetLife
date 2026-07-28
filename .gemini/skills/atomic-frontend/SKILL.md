---
name: atomic-frontend
description: Criar e modificar componentes React no frontend PetLife seguindo Atomic Design com Vanilla CSS e design tokens. Use esta skill ao criar qualquer componente novo, página, ou hook de aplicação.
---

# Skill: Atomic Frontend — PetLife

## Quando Usar Esta Skill
- Criar um novo componente (atom, molecule, organism)
- Criar uma nova página
- Criar um hook de aplicação
- Criar um adapter HTTP
- Corrigir estilos ou débitos de UX

## Contexto do Projeto
- **Framework:** React 19 + TypeScript 5.7
- **Arquitetura:** Hexagonal (Ports & Adapters) + Atomic Design
- **Estilo:** Vanilla CSS com variáveis semânticas (PROIBIDO Tailwind)
- **Testes:** Vitest + React Testing Library
- **Design Tokens:** `apps/web/src/theme.css` — LEIA ANTES

## Hierarquia Atomic Design

```
atoms/      → Unidades básicas: Button, Input, Label, Icon
molecules/  → Combinações: FormField, PetCard, Modal, Toast, VetCard
organisms/  → Funcionalidades: LoginForm, PetForm, VaccineForm, WeightChart
templates/  → Layouts de página: AppLayout, AuthLayout
pages/      → Wrappers (apenas orquestração, zero lógica visual)
```

## Estrutura de Arquivo Obrigatória

```
components/{nivel}/{NomeComponente}/
├── index.tsx      ← Componente principal
└── styles.css     ← Estilos com var(--token)
```

## Templates de Código

### 1. Atom — Exemplo: Button

**components/atoms/Button/index.tsx:**
```tsx
import React from 'react';
import './styles.css';

interface ButtonProps {
  label: string;
  variant?: 'primary' | 'secondary' | 'ghost' | 'danger';
  size?: 'sm' | 'md' | 'lg';
  disabled?: boolean;
  loading?: boolean;
  fullWidth?: boolean;
  onClick?: () => void;
  type?: 'button' | 'submit' | 'reset';
  id?: string;
  'aria-label'?: string;
}

export const Button: React.FC<ButtonProps> = ({
  label,
  variant = 'primary',
  size = 'md',
  disabled = false,
  loading = false,
  fullWidth = false,
  onClick,
  type = 'button',
  id,
  'aria-label': ariaLabel,
}) => {
  return (
    <button
      id={id}
      type={type}
      className={`btn btn--${variant} btn--${size} ${fullWidth ? 'btn--full' : ''}`}
      disabled={disabled || loading}
      onClick={onClick}
      aria-label={ariaLabel || label}
      aria-busy={loading}
    >
      {loading ? (
        <span className="btn__spinner" aria-hidden="true" />
      ) : (
        label
      )}
    </button>
  );
};

export default Button;
```

**components/atoms/Button/styles.css:**
```css
/* Usar SEMPRE var(--token) — NUNCA valores hardcoded */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-base);
  border: none;
  border-radius: var(--radius-md);
  font-family: var(--font-label);
  font-weight: var(--text-label-md-weight);
  cursor: pointer;
  transition: var(--transition-smooth);
  width: 100%; /* Atoms são full-width */
}

/* Variantes */
.btn--primary {
  background: var(--color-primary);
  color: var(--color-on-primary);
  box-shadow: var(--shadow-button);
}

.btn--primary:hover:not(:disabled) {
  box-shadow: var(--shadow-button-hover);
  transform: translateY(-1px);
}

.btn--secondary {
  background: var(--color-secondary);
  color: var(--color-on-secondary);
}

.btn--ghost {
  background: transparent;
  color: var(--color-primary);
  border: 1.5px solid var(--color-primary);
}

.btn--danger {
  background: var(--color-error);
  color: var(--color-on-error);
}

/* Tamanhos */
.btn--sm {
  font-size: var(--text-label-sm-size);
  padding: var(--space-base) var(--space-xs);
  min-height: 32px;
}

.btn--md {
  font-size: var(--text-label-md-size);
  padding: var(--space-xs) var(--space-sm);
  min-height: 44px; /* Acessibilidade — touch target mínimo */
}

.btn--lg {
  font-size: var(--text-body-md-size);
  padding: var(--space-sm) var(--space-md);
  min-height: 52px;
}

/* Estados */
.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.btn--full {
  width: 100%;
}

/* Spinner de loading */
.btn__spinner {
  width: 16px;
  height: 16px;
  border: 2px solid currentColor;
  border-right-color: transparent;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
```

### 2. Molecule — Exemplo: FormField

**components/molecules/FormField/index.tsx:**
```tsx
import React from 'react';
import './styles.css';

interface FormFieldProps {
  id: string;
  label: string;
  error?: string;
  hint?: string;
  required?: boolean;
  children: React.ReactNode;
}

export const FormField: React.FC<FormFieldProps> = ({
  id,
  label,
  error,
  hint,
  required,
  children,
}) => {
  return (
    <div className={`form-field ${error ? 'form-field--error' : ''}`}>
      <label className="form-field__label" htmlFor={id}>
        {label}
        {required && <span className="form-field__required" aria-label="obrigatório">*</span>}
      </label>
      <div className="form-field__input">
        {children}
      </div>
      {hint && !error && (
        <p className="form-field__hint">{hint}</p>
      )}
      {error && (
        <p className="form-field__error" role="alert" id={`${id}-error`}>
          {error}
        </p>
      )}
    </div>
  );
};
```

### 3. Hook de Aplicação — Exemplo: useVaccinations

**application/vaccination/useVaccinations.ts:**
```ts
import { useState, useCallback } from 'react';
import { vaccinationApi } from '../../infrastructure/http/vaccination.api';
import type { VaccinationResponse } from '../../infrastructure/dto/VaccinationResponse';

// Hook na camada Application — usa adapter de infra
// NUNCA importa de components/ aqui (violação de DIP)

export function useVaccinations(petId: string) {
  const [vaccinations, setVaccinations] = useState<VaccinationResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchVaccinations = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await vaccinationApi.list(petId);
      setVaccinations(data);
    } catch (err) {
      setError('Erro ao carregar vacinas');
    } finally {
      setLoading(false);
    }
  }, [petId]);

  const deleteVaccination = useCallback(async (vaccinationId: string) => {
    await vaccinationApi.delete(petId, vaccinationId);
    await fetchVaccinations();
  }, [petId, fetchVaccinations]);

  return {
    vaccinations,
    loading,
    error,
    fetchVaccinations,
    deleteVaccination,
  };
}
```

### 4. HTTP Adapter

**infrastructure/http/vaccination.api.ts:**
```ts
import { api } from './api';
import type { VaccinationResponse } from '../dto/VaccinationResponse';
import type { CreateVaccinationRequest } from '../dto/CreateVaccinationRequest';

// Adapter HTTP — único lugar onde axios/api é chamado para vacinas
// Segue padrão: resource-specific functions

export const vaccinationApi = {
  list: async (petId: string): Promise<VaccinationResponse[]> => {
    const { data } = await api.get(`/pets/${petId}/vaccines`);
    return data.data;
  },

  create: async (petId: string, payload: CreateVaccinationRequest): Promise<VaccinationResponse> => {
    const { data } = await api.post(`/pets/${petId}/vaccines`, payload);
    return data.data;
  },

  update: async (petId: string, id: string, payload: Partial<CreateVaccinationRequest>): Promise<VaccinationResponse> => {
    const { data } = await api.put(`/pets/${petId}/vaccines/${id}`, payload);
    return data.data;
  },

  delete: async (petId: string, id: string): Promise<void> => {
    await api.delete(`/pets/${petId}/vaccines/${id}`);
  },

  uploadProof: async (petId: string, id: string, file: File): Promise<void> => {
    const formData = new FormData();
    formData.append('file', file);
    await api.post(`/pets/${petId}/vaccines/${id}/proof`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
};
```

## Regras de CSS Obrigatórias

```css
/* ✅ CORRETO */
.meu-componente {
  background: var(--color-surface-container);
  color: var(--color-on-surface);
  padding: var(--space-sm);
  border-radius: var(--radius-md);
  font-family: var(--font-body);
  box-shadow: var(--shadow-card);
}

/* ❌ ERRADO — hardcoded */
.meu-componente {
  background: #eceef0;
  color: #191c1e;
  padding: 16px;
  border-radius: 0.75rem;
}
```

## Checklist de Componente Novo

- [ ] Pasta criada em nível correto do Atomic Design
- [ ] `index.tsx` com TypeScript estrito (sem `any`)
- [ ] `styles.css` usando apenas `var(--token)` do `theme.css`
- [ ] Atoms: `width: 100%`, sem `margin` externas
- [ ] IDs únicos em elementos interativos (para testes)
- [ ] `aria-*` nos elementos interativos
- [ ] `role="alert"` em mensagens de erro
- [ ] Hook em `application/{modulo}/` se houver lógica
- [ ] Adapter HTTP em `infrastructure/http/` se chamar API
- [ ] Testes com RTL em `*.spec.tsx`
- [ ] Nenhum import de `components/` em `application/`

## Referências Internas
- `apps/web/src/theme.css` — todos os tokens de design
- `apps/web/src/index.css` — animações e resets globais
- `docs/ADR-002-atomic-design-vanilla-css.md` — decisão arquitetural
- `docs/DOD.md` — critérios de qualidade
