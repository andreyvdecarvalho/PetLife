---
name: design-system
description: >
  Skill do Design System do PetLife. Ativa quando o agente precisa implementar,
  revisar ou verificar componentes visuais com base no protótipo Stitch.
  Contém todos os tokens de design, paleta de cores, tipografia, componentes atômicos
  e especificações de layout das 35 telas do projeto.
  Palavras-chave: design, UI, UX, CSS, tokens, cores, tipografia, componentes, layout,
  Stitch, Figma, theme, paleta, Button, Input, Card, Badge, Avatar, BottomNavigation,
  TopBar, Modal, Toast, Dashboard, Login, Cadastro, Perfil, Medicamentos, Rotina, Memórias.
---

# Skill: Design System — PetLife

> **REFERÊNCIA PRINCIPAL:** Leia o arquivo completo antes de implementar qualquer componente visual:
> [`D:/projetos-particular/PetLife/.agents/DESIGN.md`](file:///D:/projetos-particular/PetLife/.agents/DESIGN.md)
>
> Este arquivo contém **1615 linhas** extraídas diretamente do protótipo Stitch com valores CSS exatos.

---

## Princípios Mandatórios

1. **Nunca invente valores** — todo `color`, `font-size`, `border-radius`, `box-shadow` DEVE vir do `DESIGN.md` ou do `theme.css`.
2. **Sem Tailwind** — o projeto usa **Vanilla CSS** com Custom Properties definidas em `src/theme.css`.
3. **Sem CSS Modules / CSS-in-JS** — apenas seletores CSS tradicionais com classes BEM-like baseadas no nome do componente.
4. **Atomic Design** — siga rigorosamente `atoms/ → molecules/ → organisms/ → templates/ → pages/`.
5. **Material Symbols Outlined** — todos os ícones usam a icon font (não SVG inline, exceto logos de terceiros como Google/Apple OAuth).

---

## Resumo Rápido dos Design Tokens

### Cores Principais

```css
/* Primária (Laranja-Marrom) */
--color-primary:              #9b4500;   /* CTAs, botões, destaques */
--color-primary-container:    #ff914d;   /* Tab ativa, avatar ring */
--color-on-primary-container: #6e2f00;   /* Texto sobre primary-container */

/* Secundária (Azul) */
--color-secondary:            #005fac;   /* Links, focus ring */
--color-secondary-container:  #5fa6fd;   /* Badge de medicação */

/* Terciária (Verde-Teal) */
--color-tertiary:             #006b55;   /* Status positivo, saúde */
--color-tertiary-container:   #68bda2;   /* Badge ativo, card passeio */

/* Superfícies */
--color-surface:                    #f7f9fb;  /* Fundo geral */
--color-surface-container-lowest:   #ffffff;  /* Cards, modais */
--color-surface-container-low:      #f2f4f6;  /* Inputs, chips info */
--color-on-surface:                 #191c1e;  /* Texto principal */
--color-on-surface-variant:         #564339;  /* Texto secundário */

/* Erro */
--color-error:                #ba1a1a;
--color-error-container:      #ffdad6;

/* Bordas */
--color-outline:              #897267;
--color-outline-variant:      #dcc1b4;
```

### Tipografia

| Role | Fonte | Tamanho | Peso |
|---|---|---|---|
| Display | Quicksand | 48px | 700 |
| Headline LG | Quicksand | 32px (mobile: 28px) | 700 |
| Headline MD | Quicksand | 24px | 600 |
| Body LG | Plus Jakarta Sans | 18px | 400 |
| Body MD | Plus Jakarta Sans | 16px | 400 |
| Label MD | Plus Jakarta Sans | 14px | 600 |
| Label SM | Plus Jakarta Sans | 12px | 500 |

### Border Radius

| Token | Valor | Uso |
|---|---|---|
| `--radius-lg` | 8px | Inputs |
| `--radius-xl` | 12px | Cards pequenos |
| `--radius-2xl` | 16px | Cards principais |
| `--radius-3xl` | 24px | Ilustrações |
| `--radius-full` | 9999px | Botões CTA, avatars, badges |

### Sombras

```css
--shadow-card:          0 4px 20px rgba(0,0,0,0.06);
--shadow-button:        0 4px 12px rgba(155,69,0,0.2);
--shadow-button-hover:  0 6px 15px rgba(155,69,0,0.3);
--shadow-modal:         0 8px 32px rgba(0,0,0,0.12);
--shadow-bottom-nav:    0 -4px 20px rgba(0,0,0,0.06);
```

---

## Componentes Críticos (resumo)

### Button Primário (CTA)
```css
/* Atoms/Button/styles.css */
.atom-button {
  width: 100%;
  padding: 12px 16px;
  border-radius: 9999px;
  background: var(--color-primary);
  color: var(--color-on-primary);
  font-family: var(--font-label);
  font-size: 14px;
  font-weight: 600;
  border: 1px solid transparent;
  box-shadow: var(--shadow-button);
  transition: all 0.2s ease;
}
.atom-button:hover:not(:disabled) { opacity: 0.9; box-shadow: var(--shadow-button-hover); }
.atom-button:active:not(:disabled) { transform: scale(0.98); }
```

### Input com Ícone
```css
/* Atoms/Input/styles.css */
.atom-input-wrapper { position: relative; width: 100%; }
.atom-input {
  width: 100%;
  background: #F1F5F9;
  border: 1px solid transparent;
  border-radius: 8px;
  padding: 12px 16px;
  font-size: 16px;
  color: var(--color-on-surface);
}
.atom-input:focus { border-color: var(--color-secondary); box-shadow: 0 0 0 1px var(--color-secondary); outline: none; }
.atom-input__icon-left {
  position: absolute; top: 50%; left: 16px;
  transform: translateY(-50%);
  color: var(--color-on-surface-variant);
  opacity: 0.5;
  pointer-events: none;
}
.atom-input--with-icon-left { padding-left: 44px; }
```

### Card Principal
```css
.organism-card {
  background: var(--color-surface-container-lowest);
  border-radius: 16px;  /* --radius-2xl */
  padding: 24px;        /* --spacing-md */
  box-shadow: var(--shadow-card);
  border: 1px solid var(--color-surface-variant);
}
```

### Bottom Navigation
```css
.organism-bottom-nav {
  position: fixed; bottom: 0; left: 0; right: 0;
  height: 72px;
  background: var(--color-surface-container-lowest);
  border-radius: 12px 12px 0 0;
  box-shadow: var(--shadow-bottom-nav);
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding-bottom: env(safe-area-inset-bottom);
  z-index: 50;
}
.organism-bottom-nav__tab--active {
  background: var(--color-primary-container);
  color: var(--color-on-primary-container);
  border-radius: 9999px;
  padding: 4px 16px;
}
.organism-bottom-nav__tab--inactive {
  color: var(--color-on-surface-variant);
}
```

---

## Paleta de Cores por Tipo de Badge

| Tipo | Background | Texto | Uso |
|---|---|---|---|
| Urgente/Vacina | `error-container` (#ffdad6) | `on-error-container` (#93000a) | Próximos vencimentos |
| Medicação | `secondary-container` (#5fa6fd) | `on-secondary-container` (#003a6d) | Doses de remédio |
| Grooming/Banho | `tertiary-container` (#68bda2) | `on-tertiary-container` (#004b3a) | Higiene |
| Tomado ✓ | `tertiary-fixed` (#9df3d6) | `tertiary` (#006b55) | Concluído |
| Pendente | `error-container` (#ffdad6) | `on-error-container` (#93000a) | Não tomado |
| Neutro | `surface-container-high` (#e6e8ea) | `on-surface-variant` (#564339) | Data futura |

---

## Mapa das 35 Telas (Stitch ID → Rota React)

| Tela | Stitch ID | Rota |
|---|---|---|
| Login | `fee8d00b81bc4f0ea62e4a29e27350d9` | `/login` |
| Cadastro | `2030d318e29b43e680a9b5b2c50db154` | `/register` |
| Dashboard | `61b4a03107ab44c096db3aa6c02fc3c9` | `/` |
| Perfil do Pet | `974c612fe4c744adaf3aae75cc039666` | `/pets/:id` |
| Controle de Medicamentos | `2f0b52ee106f48c9b3f105ad561bdfb5` | `/medications` |
| Gestão de Rotina | `edd5938791e547a48fb6f99d91b9945c` | `/routine` |
| Diário de Memórias | `165a4f285dc04599b1bd540e6bda0870` | `/memories` |
| Agendamento Data/Hora | `0d72497c74464f2884b1e6227b6244de` | `/schedule/datetime` |
| Agendamento Profissional | `00bd093204194c44bbb3c901be20d7a2` | `/schedule/professional` |
| Meus Agendamentos | `7c874294a3da4a01b97b19dad8c121f4` | `/appointments` |
| Adicionar Registro de Saúde | `5ca22861d0244f8e86138932b2342a3a` | `/health/new` |
| Histórico de Saúde | `b78c5341ef4044d38059dc71d9d9295d` | `/health/history` |
| Onboarding - Boas-vindas | `57bb50529d4c4a9b8bb463c6b2661618` | `/onboarding` |
| Onboarding - Dados Básicos | `83d5230c80554ab5a343cdad4f6ed2e1` | `/onboarding/pet-info` |
| Onboarding - Foto do Pet | `942b4590a86f48b386c2912255739974` | `/onboarding/pet-photo` |
| Onboarding - Saúde e Perfil | `ad9204f289244e52b4e81506109a247e` | `/onboarding/pet-health` |
| Adicionar Medicamento - Detalhes | `139dba00eaf142e7966d7e3de18f4dde` | `/medications/new/details` |
| Adicionar Medicamento - Horários | `935af12b40ce4e49b110803aad76a3bf` | `/medications/new/schedule` |
| Adicionar Compromisso | `6947ce61a3454922ba720a6f83a5d6cd` | `/routine/new` |
| Adicionar Nova Memória | `3a17a942719447cb933e4246ddc68da3` | `/memories/new` |
| Adicionar Registro de Saúde | `5ca22861d0244f8e86138932b2342a3a` | `/health/new` |
| Comprovante Agendamento | `7630f54009194990a20d427fd4ff51b8` | `/appointments/:id/receipt` |
| Agendamento Confirmado | `dd979b2512b64bb0871ca056a4e6a321` | `/appointments/:id/confirmed` |

---

## Ícones por Tela (Material Symbols Outlined)

| Tela | Ícones Utilizados |
|---|---|
| Dashboard | `pets`, `vaccines`, `pill`, `schedule`, `notifications`, `add`, `medical_services`, `spa`, `directions_walk` |
| Perfil do Pet | `favorite`, `cake`, `pets`, `scale`, `vaccines`, `medical_services`, `edit`, `share`, `description` |
| Medicamentos | `pill`, `schedule`, `done`, `add`, `water_drop`, `vaccines`, `sanitizer` |
| Rotina | `directions_walk`, `spa`, `restaurant`, `medical_services`, `school`, `add`, `done`, `schedule` |
| Memórias | `photo_camera`, `cake`, `favorite`, `star`, `park`, `add_photo_alternate`, `auto_stories` |
| Agendamento | `location_on`, `calendar_today`, `schedule`, `person`, `medical_services`, `check_circle` |
| Login/Cadastro | `mail`, `lock`, `visibility`, `visibility_off`, `pets`, `person`, `arrow_forward` |
| Bottom Nav | `home`, `pets`, `event`, `auto_stories`, `person` |

---

## Fluxo de Implementação por Tela

Ao implementar qualquer tela nova:
1. **Leia a seção correspondente** no `DESIGN.md` (Capítulo 8)
2. **Identifique os componentes** atômicos necessários (Capítulo 7)
3. **Verifique as cores** usadas no layout (Capítulo 2.2)
4. **Use apenas tokens** de `src/theme.css` (Capítulo 1)
5. **Crie os testes** antes do componente (seguir `tdd-frontend` skill)
6. **Valide acessibilidade** (WCAG 2.1 AA — contraste mínimo 4.5:1)
