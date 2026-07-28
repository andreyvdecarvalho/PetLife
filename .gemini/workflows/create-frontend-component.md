# Workflow: Criar Componente Frontend

## Quando Usar
Use este workflow ao criar qualquer componente novo, hook ou tela no frontend.
Garante conformidade com Atomic Design, DOD e o design system PetLife.

## Pré-requisitos
- [ ] Ler `.gemini/skills/atomic-frontend/SKILL.md`
- [ ] Ler `.gemini/skills/ux-design-petlife/SKILL.md`
- [ ] Verificar `apps/web/src/theme.css` para tokens disponíveis

## Passos

### Fase 1: Planejamento (2–5 min)
1. Identificar o nível do componente:
   - **Atom**: elemento básico sem dependências de outros componentes
   - **Molecule**: combina atoms + lógica simples
   - **Organism**: funcionalidade completa (formulário, lista, etc.)
   - **Template**: layout de página
   - **Page**: apenas orquestração (import organisms + providers)

2. Verificar se já existe componente similar:
   ```bash
   ls apps/web/src/components/atoms/
   ls apps/web/src/components/molecules/
   ls apps/web/src/components/organisms/
   ```

3. Verificar se precisa de hook novo:
   ```bash
   ls apps/web/src/application/
   ```

4. Verificar se precisa de adapter HTTP:
   ```bash
   ls apps/web/src/infrastructure/http/
   ```

### Fase 2: Criar Estrutura
```bash
# Exemplo: criar um novo Organism "MedicationCard"
LEVEL="molecules"  # ou atoms, organisms, templates
NAME="MedicationCard"

mkdir -p apps/web/src/components/${LEVEL}/${NAME}
touch apps/web/src/components/${LEVEL}/${NAME}/index.tsx
touch apps/web/src/components/${LEVEL}/${NAME}/styles.css
```

### Fase 3: Implementar o Componente

**index.tsx — Regras:**
- TypeScript estrito (sem `any`)
- Props com interface nomeada
- Export nomeado (`export const NomeComponente`)
- IDs únicos em elementos interativos
- `aria-*` em elementos interativos

**styles.css — Regras:**
- APENAS `var(--token)` do `theme.css`
- Atoms: `width: 100%`, sem `margin` externas
- Hover/focus/active states em interativos
- Mobile-first

### Fase 4: Hook de Aplicação (se necessário)
```bash
# Criar hook
touch apps/web/src/application/{modulo}/use{Funcionalidade}.ts
```
- **NUNCA** importar de `components/` no hook
- Usar adapter de `infrastructure/http/` para chamadas API

### Fase 5: Adapter HTTP (se necessário)
```bash
# Atualizar adapter existente ou criar novo
cat apps/web/src/infrastructure/http/{modulo}.api.ts
```

### Fase 6: Testes
```bash
# Criar arquivo de teste
touch apps/web/src/components/{nivel}/{Nome}/{Nome}.spec.tsx
```

**Template de teste RTL:**
```tsx
import { render, screen, fireEvent } from '@testing-library/react';
import { MeuComponente } from './index';

describe('MeuComponente', () => {
  it('deve renderizar sem erros', () => {
    render(<MeuComponente prop="valor" />);
    expect(screen.getByRole('button')).toBeInTheDocument();
  });

  it('deve chamar callback ao clicar', () => {
    const mockFn = vi.fn();
    render(<MeuComponente onClick={mockFn} />);
    fireEvent.click(screen.getByRole('button'));
    expect(mockFn).toHaveBeenCalledOnce();
  });
});
```

### Fase 7: Verificações
```bash
cd apps/web

# Lint (zero warnings)
pnpm lint

# TypeScript (zero erros)
pnpm typecheck

# Testes
pnpm test

# Verificar violações de DIP
grep -rn "from.*components/" src/application/
```

### Fase 8: Commit
```bash
git add .
git commit -m "feat(web): adiciona componente {NomeComponente}"
```

## Exemplos Comuns

### Novo Atom: Badge de Status
```tsx
// components/atoms/StatusBadge/index.tsx
interface StatusBadgeProps {
  status: 'active' | 'pending' | 'inactive';
  label: string;
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ status, label }) => (
  <span className={`status-badge status-badge--${status}`} role="status">
    {label}
  </span>
);
```

### Novo Organism: Formulário de Consulta
```tsx
// components/organisms/ConsultationForm/index.tsx
// Usa: FormField (molecule), Button (atom), Input (atom)
// Hook: useCreateConsultation (application/consultation/)
// Adapter: consultation.api.ts (infrastructure/http/)
```

### Nova Página: Tela de Favoritos de Vets
```tsx
// pages/VetFavoritesPage.tsx
// Apenas orquestração — importa organisms
// Hook: useVetFavorites (application/veterinarian/)
// Zero estilo próprio — usa componentes
```

## Checklist Final

- [ ] Pasta criada no nível correto do Atomic Design
- [ ] `index.tsx` com TypeScript estrito
- [ ] `styles.css` usando apenas `var(--token)`
- [ ] Atoms: `width: 100%`, sem `margin` externas
- [ ] IDs únicos em elementos interativos
- [ ] `aria-*` nos elementos interativos
- [ ] `role="alert"` em mensagens de erro
- [ ] Loading states implementados
- [ ] Estado vazio com mensagem e CTA
- [ ] Hook em `application/` sem imports de `components/`
- [ ] Adapter HTTP em `infrastructure/http/` se chamou API
- [ ] Testes com RTL passando
- [ ] `pnpm lint && pnpm typecheck` — zero erros

## Referências
- `.gemini/skills/atomic-frontend/SKILL.md`
- `.gemini/skills/ux-design-petlife/SKILL.md`
- `apps/web/src/theme.css` — tokens
- `docs/DOD.md` — critérios de qualidade
