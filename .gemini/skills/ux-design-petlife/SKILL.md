---
name: ux-design-petlife
description: Projetar e implementar interfaces seguindo o design system do PetLife. Use esta skill ao criar novas telas, componentes visuais, ou ao melhorar a experiência do usuário existente.
---

# Skill: UX Design PetLife

## Quando Usar Esta Skill
- Criar uma nova tela ou seção de interface
- Melhorar a experiência de um fluxo existente
- Corrigir débitos de UX (DT-04 a DT-10)
- Implementar micro-animações ou transições
- Revisar acessibilidade de componentes

## Identidade Visual do PetLife

### Paleta de Cores
```
Primária (Laranja-terra): #9b4500 → var(--color-primary)
  └── Containers: #ff914d → var(--color-primary-container)
  
Secundária (Azul): #005fac → var(--color-secondary)
  └── Containers: #5fa6fd → var(--color-secondary-container)
  
Terciária (Verde): #006b55 → var(--color-tertiary)
  └── Containers: #68bda2 → var(--color-tertiary-container)
  
Erro: #ba1a1a → var(--color-error)
Fundo: #f7f9fb → var(--color-background)
Texto: #191c1e → var(--color-on-surface)
```

### Tipografia
```
Headlines: Quicksand (600–700) → var(--font-headline)
Body: Plus Jakarta Sans (400–600) → var(--font-body)
Labels: Plus Jakarta Sans (600) → var(--font-label)

Escala:
  Display LG: 48px/56px
  Headline LG: 32px/40px (mobile: 28px/36px)
  Headline MD: 24px/32px
  Body LG: 18px/28px
  Body MD: 16px/24px
  Label MD: 14px/20px (tracking: 0.01em)
  Label SM: 12px/16px
```

### Espaçamento (Sistema de 4px)
```
--space-base: 4px
--space-xs:   8px
--space-sm:   16px
--space-md:   24px
--space-lg:   40px
--space-xl:   64px
```

## Princípios de Design PetLife

### 1. Caloroso e Confiável
- Paleta quente (laranja-terra) transmite cuidado e proximidade
- Cantos arredondados (radius-md a radius-xl) criam sensação de suavidade
- Sombras sutis elevam cards sem ser pesados

### 2. Clareza e Hierarquia Visual
- Hierarquia tipográfica clara: headline > body > label
- Uso de cor semântica: verde = sucesso/saúde, azul = ação, laranja = identidade
- White space generoso — mínimo `--space-sm` entre elementos

### 3. Micro-animações Premium
```css
/* Transição padrão — usar em todos os elementos interativos */
transition: var(--transition-smooth); /* all 0.3s cubic-bezier(0.4, 0, 0.2, 1) */

/* Hover em cards */
.card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-modal);
}

/* Hover em botões */
.btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: var(--shadow-button-hover);
}

/* Entrada de elementos */
@keyframes slideUp {
  from { opacity: 0; transform: translateY(16px); }
  to { opacity: 1; transform: translateY(0); }
}
.animate-in {
  animation: slideUp 0.3s cubic-bezier(0.4, 0, 0.2, 1) forwards;
}
```

### 4. Feedback Visual Consistente
```
Estados de loading:
  - Skeleton screens para listas
  - Spinner no botão de ação
  - Texto "Carregando..." como fallback

Estados vazios:
  - Ilustração + título + descrição + CTA
  - Ex: "Nenhuma vacina registrada. Adicione a primeira!"

Estados de erro:
  - Inline: campo vermelho + mensagem de erro
  - Toast: mensagem temporária (3s) + ícone
  - Página: componente de erro + botão "Tentar novamente"
```

## Padrões de Componentes PetLife

### Card de Pet
```css
.pet-card {
  background: var(--color-surface-container-low);
  border-radius: var(--radius-xl);
  padding: var(--space-sm);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--color-outline-variant);
  transition: var(--transition-smooth);
}

.pet-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-modal);
  border-color: var(--color-primary-container);
}
```

### Timeline de Eventos
```css
/* Cada tipo de evento tem sua cor */
.timeline-item--vaccine { 
  border-left: 3px solid var(--color-tertiary); 
}
.timeline-item--consultation { 
  border-left: 3px solid var(--color-secondary); 
}
.timeline-item--medication { 
  border-left: 3px solid var(--color-primary); 
}
.timeline-item--grooming { 
  border-left: 3px solid #c2185b; /* rosa — adicionar token */ 
}
```

### Formulários
```css
.form-input {
  width: 100%;
  padding: var(--space-xs) var(--space-sm);
  border: 1.5px solid var(--color-outline-variant);
  border-radius: var(--radius-md);
  font-family: var(--font-body);
  font-size: var(--text-body-md-size);
  color: var(--color-on-surface);
  background: var(--color-surface-container-lowest);
  transition: var(--transition-smooth);
}

.form-input:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(155, 69, 0, 0.12);
  outline: none;
}

.form-input.error {
  border-color: var(--color-error);
  box-shadow: 0 0 0 3px rgba(186, 26, 26, 0.08);
}
```

### Bottom Navigation (Mobile)
```css
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: var(--color-surface-container-lowest);
  box-shadow: var(--shadow-bottom-nav);
  display: flex;
  justify-content: space-around;
  padding: var(--space-xs) 0;
  padding-bottom: calc(var(--space-xs) + env(safe-area-inset-bottom));
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: var(--space-base) var(--space-xs);
  color: var(--color-outline);
  font-size: var(--text-label-sm-size);
  font-family: var(--font-label);
  border-radius: var(--radius-md);
  transition: var(--transition-smooth);
}

.nav-item--active {
  color: var(--color-primary);
  background: var(--color-primary-fixed);
}
```

## Acessibilidade Obrigatória

### Contraste Mínimo
```
Texto normal: 4.5:1
Texto grande (18px+): 3:1
Elementos interativos: 3:1
```

### Elementos Interativos
```tsx
/* ✅ Correto */
<button
  aria-label="Excluir vacina da Luna"
  aria-busy={loading}
  disabled={loading}
>
  {loading ? <Spinner /> : 'Excluir'}
</button>

/* ✅ Formulários */
<label htmlFor="pet-name">Nome do pet *</label>
<input
  id="pet-name"
  type="text"
  aria-required="true"
  aria-describedby="pet-name-error"
/>
<p id="pet-name-error" role="alert">
  {nameError}
</p>
```

### Touch Targets (Mobile)
- Mínimo: 44x44px para elementos interativos
- Ideal: 48x48px
- Espaçamento entre targets: mínimo 8px

## Fluxos de UX Críticos

### Fluxo 1: Registro de Vacina (< 3 taps/cliques)
1. Tap no card do pet → Perfil do pet
2. Tap em "Vacinas" → Lista de vacinas
3. Tap em "+" → Modal/Sheet de registro
4. Preencher e salvar

### Fluxo 2: Marcar Dose de Medicamento
1. Notificação recebida → Tap "Marcar como tomado"
2. Ou: Dashboard → Card de dose pendente → Tap

### Fluxo 3: Onboarding (< 5 minutos)
1. Cadastro (email/senha ou Google) → 30s
2. Aceite de termos → 15s
3. Cadastrar primeiro pet (nome + espécie) → 2 min
4. Dashboard principal → imediato

## Responsive Design

### Breakpoints
```css
/* Mobile first */
/* xs: < 480px */
/* sm: 480px - 768px */
/* md: 768px - 1024px */
/* lg: > 1024px */

@media (min-width: 768px) {
  /* Tablet / Desktop adaptations */
  .container {
    max-width: var(--max-width); /* 1200px */
    margin: 0 auto;
    padding: 0 var(--space-md);
  }
}
```

### Layout Patterns
```
Mobile: Stack vertical, bottom navigation
Tablet: 2 colunas, sidebar compacta
Desktop: Sidebar fixa, conteúdo central max 800px
```

## Checklist de Qualidade UX

- [ ] Todas as cores usam `var(--token)` do `theme.css`
- [ ] Hover/focus/active states implementados
- [ ] Loading states (skeleton ou spinner)
- [ ] Estado vazio com mensagem útil e CTA
- [ ] Mensagens de erro claras e acionáveis
- [ ] Touch targets >= 44x44px
- [ ] Contraste de texto >= 4.5:1
- [ ] Labels em todos os inputs (`htmlFor` + `id`)
- [ ] `aria-label` em botões de ícone
- [ ] `role="alert"` em mensagens de erro
- [ ] Animações respeitam `prefers-reduced-motion`
- [ ] Funciona em mobile (375px width)
- [ ] Funciona em teclado (tab navigation)

## Referências
- `apps/web/src/theme.css` — todos os tokens
- `docs/ADR-002-atomic-design-vanilla-css.md`
- `docs/PRD.md` seção RNF-007 (usabilidade e acessibilidade)
- WCAG 2.1 Nível AA
