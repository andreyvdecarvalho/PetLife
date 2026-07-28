# 🐾 GEMINI.md — Documentação para Agentes de IA — PetLife

> **Versão:** 1.0.0 | **Atualizado em:** 27 de Julho de 2026
> Este arquivo é lido automaticamente por agentes IA (Google Antigravity, Gemini CLI, etc.) para entender o projeto antes de qualquer ação.

---

## 📋 Índice Rápido

1. [Identidade do Projeto](#1-identidade-do-projeto)
2. [Mapa da Arquitetura](#2-mapa-da-arquitetura)
3. [Monorepo — Estrutura de Diretórios](#3-monorepo--estrutura-de-diretórios)
4. [Stack Tecnológica](#4-stack-tecnológica)
5. [Convenções de Código Obrigatórias](#5-convenções-de-código-obrigatórias)
6. [Mapa de Débitos](#6-mapa-de-débitos)
7. [Design System e UI/UX](#7-design-system-e-uiux)
8. [Módulos do Sistema](#8-módulos-do-sistema)
9. [Fluxos de Trabalho](#9-fluxos-de-trabalho)
10. [Skills Disponíveis](#10-skills-disponíveis)
11. [Comandos Essenciais](#11-comandos-essenciais)
12. [Regras de Ouro para Agentes](#12-regras-de-ouro-para-agentes)

---

## 1. Identidade do Projeto

| Campo | Valor |
|---|---|
| **Nome** | PetLife |
| **Tagline** | *Toda a vida do seu pet em um só lugar* |
| **Tipo** | Plataforma multiplataforma — Android, iOS, Web (PWA) |
| **PRD** | `docs/PRD.md` (v3.0) |
| **DOD** | `docs/DOD.md` — leitura obrigatória antes de qualquer PR |
| **Conformidade** | `docs/compliance-report.md` — gap analysis PRD vs implementação |
| **ADRs** | `docs/adr/` — decisões arquiteturais registradas |
| **Fase Atual** | MVP — Fase 1 (Jul–Out 2026) |
| **Cobertura API** | ~80% (47/65 endpoints implementados) |

### Missão
Centralizar o histórico de saúde, desenvolvimento e rotina dos animais de estimação, permitindo que tutores acompanhem informações, recebam lembretes automáticos e compartilhem dados com profissionais de forma segura (LGPD).

---

## 2. Mapa da Arquitetura

### Visão Geral
```
CLIENTES
  ├── React Native (Android/iOS)
  └── React 19 PWA (Web)
       |
  EDGE / CDN (CloudFront / Cloudflare)
       |
  API GATEWAY (Rate Limiting + Auth JWT RS256)
       |
  ┌────────────────────────────┐
  │ Serviços Backend           │
  │ Auth | Pet | Events        │
  │ Notification | File | PDF  │
  └──────────┬─────────────────┘
             |──── RabbitMQ ────► FCM / APNs / Email
             |──── PostgreSQL
             |──── Redis Cache
             └──── S3 / MinIO
```

### Arquitetura Hexagonal (Ports & Adapters) — Backend

```
com.petlife.modules.{module}/
├── domain/
│   ├── entity/          <- POJOs puros (SEM @Entity, SEM Spring)
│   └── port/            <- Interfaces de repositório e serviços externos
├── application/
│   └── usecase/         <- Casos de uso (SEM imports de infraestrutura)
└── infrastructure/
    ├── controller/      <- REST Controllers + DTOs de request/response
    ├── persistence/
    │   ├── entity/      <- @Entity JPA (XJpaEntity)
    │   ├── mapper/      <- Conversão domínio <-> JPA
    │   ├── repository/  <- JpaRepository<XJpaEntity, UUID>
    │   └── adapter/     <- XPersistenceAdapter implements XRepositoryPort
    └── dto/             <- DTOs de API (Request/Response)
```

### Arquitetura Hexagonal — Frontend

```
apps/web/src/
├── domain/              <- Tipos e interfaces de domínio puro
├── application/         <- Hooks de casos de uso (React hooks)
│   ├── auth/  pet/  vaccination/  medications/  consultation/
│   ├── grooming/  routine/  notification/  user/  veterinarian/
├── infrastructure/      <- Adaptadores de infra
│   ├── http/            <- Chamadas API (api.ts, *.api.ts)
│   ├── browser/         <- DOM, localStorage, geolocation
│   ├── firebase/        <- FCM
│   ├── dto/             <- Tipos de resposta da API
│   └── storage/         <- Armazenamento local
└── components/          <- Atomic Design
    ├── atoms/           <- Button, Input, Label
    ├── molecules/       <- FormField, PetCard, Modal, Toast
    ├── organisms/       <- LoginForm, PetForm, VaccineForm, etc.
    ├── templates/       <- Layouts de página
    └── pages/           <- Wrappers de página (apenas orquestração)
```

---

## 3. Monorepo — Estrutura de Diretórios

```
PetLife/
├── GEMINI.md                    <- VOCÊ ESTÁ AQUI
├── README.md / SECURITY.md
├── package.json                 <- pnpm workspace raiz
├── pnpm-workspace.yaml / pnpm-lock.yaml
├── docker-compose.yml           <- PostgreSQL + Redis + RabbitMQ + MinIO
├── .github/workflows/           <- CI/CD GitHub Actions
├── .gemini/
│   ├── skills/                  <- Skills para agentes IA
│   └── workflows/               <- Workflows documentados
├── docs/
│   ├── PRD.md                   <- Documento de Requisitos (v3.0)
│   ├── DOD.md                   <- Definition of Done
│   ├── compliance-report.md     <- Gap analysis PRD vs código
│   ├── SDD-hexagonal-refactoring.md
│   ├── hexagonal_refactoring_plan.md
│   └── adr/                     <- Architecture Decision Records
└── apps/
    ├── backend/                 <- Java 21 + Spring Boot 4.1.0
    │   ├── pom.xml / Dockerfile / checkstyle.xml
    │   └── src/main/java/com/petlife/
    │       ├── PetLifeApplication.java
    │       ├── config/          <- Spring config (Security, CORS, etc.)
    │       ├── shared/          <- Classes compartilhadas (PagedResult)
    │       └── modules/
    │           ├── auth/  pet/  medication/
    │           ├── notification/  veterinarian/
    └── web/                     <- React 19 + TypeScript + Vite
        ├── package.json / vite.config.ts / Dockerfile
        └── src/
            ├── domain/  application/  infrastructure/
            ├── components/  pages/  contexts/  utils/
            ├── theme.css    <- Design tokens — NAO ALTERAR sem aprovação
            └── index.css    <- Estilos globais + animações
```

---

## 4. Stack Tecnológica

### Backend
| Camada | Tecnologia | Versão |
|---|---|---|
| Linguagem | Java | 21 LTS |
| Framework | Spring Boot | 4.1.0 |
| Build | Maven | 3.9.9 |
| ORM | Hibernate / Spring Data JPA | 6.6.0 |
| Migrations | Flyway | 10.20.0 |
| Banco Principal | PostgreSQL | 16.4 |
| Cache / Sessions | Redis | 7.4.0 |
| Object Storage | AWS S3 / MinIO | — |
| Mensageria | RabbitMQ | 4.0.0 |
| Push Notifications | Firebase Admin SDK | 9.3.0 |
| PDF | OpenPDF | 2.0.3 |
| Documentação API | SpringDoc OpenAPI | 2.8.6 |
| Testes | JUnit 5 + Testcontainers | — |
| Cobertura | JaCoCo (mínimo 85%) | 0.8.12 |
| Arquitetura | ArchUnit | 1.3.0 |

### Frontend
| Camada | Tecnologia | Versão |
|---|---|---|
| Linguagem | TypeScript | 5.7.2 |
| Framework | React | 19.0.0 |
| Build | Vite | 6.4.3 |
| Roteamento | React Router DOM | 6.28.0 |
| HTTP Client | Axios | 1.18.0 |
| Gráficos | Recharts | 3.8.1 |
| Push | Firebase SDK | 12.15.0 |
| Testes | Vitest + RTL + Playwright | 3.2.6 |
| Estilo | Vanilla CSS (variáveis semânticas) | — |

### Infraestrutura
| Componente | Tecnologia |
|---|---|
| Containerização | Docker + Docker Compose |
| CI/CD | GitHub Actions |
| Monitoramento | Sentry + Grafana + Prometheus |
| Package Manager | pnpm (workspace) |

---

## 5. Convenções de Código Obrigatórias

### 5.1 Backend (Java)

**REGRAS DE ARQUITETURA HEXAGONAL — VIOLAÇÃO = PR RECUSADA**

| Regra | Descrição |
|---|---|
| `NO-JPA-IN-DOMAIN` | Entidades de domínio (`domain/entity/`) NUNCA têm `@Entity`, `@Table`, `jakarta.persistence.*` |
| `NO-SPRING-IN-DOMAIN` | Domínio é 100% POJO puro — sem `@Component`, `@Service`, `@Autowired` |
| `NO-JPA-IN-PORTS` | Ports (`domain/port/`) NUNCA usam `Page<>`, `Pageable` do Spring Data |
| `NO-HTTP-IN-USECASE` | Use Cases NUNCA retornam `ApiResponse<>` nem importam `HttpStatus` |
| `NO-JPA-IN-USECASE` | Use Cases NUNCA injetam `JpaRepository` nem `JpaEntity` |
| `ADAPTER-ONLY` | Apenas Adapters em `infrastructure/` fazem `implements XRepositoryPort` |
| `PAGED-RESULT` | Paginação usa `PagedResult<T>` (domínio); Adapters constroem `PageRequest` |
| `ONE-PUBLIC-PER-FILE` | Cada tipo `public` Java em arquivo separado com mesmo nome |
| `PACKAGE-DECLARATION` | Todo arquivo `.java` tem `package com.petlife...` na primeira linha |

**Nomenclatura:**
- Classes: `PascalCase` (ex: `CreateVaccinationUseCase`)
- Métodos/variáveis: `camelCase`
- Pacotes: `lowercase`
- Endpoints REST: `/api/v1/{recurso}` com verbos HTTP corretos
- Migrations: `V{N}__{descricao_snake_case}.sql`

**Commits:** Conventional Commits em PT-BR
```
feat: adiciona endpoint de listagem de favoritos
fix: corrige validação do CRMV no cadastro de veterinário
refactor: extrai lógica de PDF para PdfExportPort
test: adiciona testes de cobertura para CreateMedicationUseCase
```

### 5.2 Frontend (TypeScript/React)

**REGRAS DE DESIGN — VIOLAÇÃO = PR RECUSADA**

| Regra | Descrição |
|---|---|
| `NO-TAILWIND` | Tailwind CSS é proibido. Usar Vanilla CSS com variáveis semânticas |
| `NO-HARDCODED-COLORS` | Cores, fontes e espaçamentos via `var(--token)` do `theme.css` |
| `NO-INLINE-STYLES` | Estilos inline são proibidos (exceto valores dinâmicos computados) |
| `NO-ANY` | TypeScript `any` é proibido exceto em casos extremamente justificados |
| `ATOMIC-DESIGN` | Componentes: atoms → molecules → organisms → templates → pages |
| `COMPONENT-CSS` | Cada componente tem `index.tsx` + `styles.css` na sua pasta |
| `ATOMS-FULL-WIDTH` | Atoms: `width: 100%` e sem `margin` externas |
| `DIP-HOOKS` | Hooks de `application/` NUNCA importam de `components/` |
| `HTTP-ADAPTERS` | Chamadas API SOMENTE em `infrastructure/http/*.api.ts` |

---

## 6. Mapa de Débitos

> Baseado em `docs/compliance-report.md` (16/07/2026). Cobertura geral: **80%**

### 6.1 Débitos Críticos — Bloqueadores de Produção

| **DB-01** | M01 | `POST /auth/refresh` | ✅ Resolvido |
| **DB-02** | M01 | FCM token registrado no frontend | ✅ Resolvido |
| **DB-03** | M02 | `DELETE /pets/{id}` + Cascade | ✅ Resolvido |
| **DB-04** | M09 | `GET /veterinarians/favorites` | ✅ Resolvido |
| **DB-05** | M01 | Chaves RSA persistidas programaticamente | ✅ Resolvido |
| **DB-06** | M01 | Apple Sign-In não implementado | App iOS incompleto (P0 PRD) |
| **DB-07** | M01 | Rate limiting de login integrado | ✅ Resolvido |

### 6.2 Débitos de Média Prioridade

| ID | Módulo | Descrição |
|---|---|---|
| **DB-08** | M03 | `DELETE /vaccinations/{id}` | ✅ Resolvido |
| **DB-09** | M03 | Autocomplete de vacinas | ✅ Resolvido |
| **DB-10** | M03 | Upload de comprovante | ✅ Resolvido |
| **DB-11** | M04 | `PUT /consultations/{id}` | ✅ Resolvido |
| **DB-12** | M04 | `DELETE /consultations/{id}` | ✅ Resolvido |
| **DB-13** | M04 | Peso da consulta atualiza pet | ✅ Resolvido |
| **DB-14** | M04 | `reason` obrigatório | ✅ Resolvido |
| **DB-15** | M05 | `PUT /medications/{id}` | ✅ Resolvido |
| **DB-16** | M05 | Duração em dias na UI | ✅ Resolvido |
| **DB-17** | M05 | `prescribedBy` e `reason` inclusos | ✅ Resolvido |
| **DB-18** | M06 | `DELETE /groomings/{id}` | ✅ Resolvido |
| **DB-19** | M09 | `GET/PUT /veterinarians/me` | ✅ Resolvido |
| **DB-20** | M09 | Gerenciamento de endereços | ✅ Resolvido |
| **DB-21** | M09 | Gerenciamento de horários | ✅ Resolvido |
| **DB-22** | M02 | Link PDF frontend | ✅ Resolvido |
| **DB-23** | M08 | UI Preferências Notificação | ✅ Resolvido |

### 6.3 Débitos Não-Funcionais

| **RNF-01** | LGPD | Delete em cascata de pets validado | ✅ Resolvido |
| **RNF-02** | Segurança | JWT tokens sem configuração flexível |
| **RNF-03** | Disponibilidade | Redis não configurado em produção |
| **RNF-04** | Observabilidade | Sentry não integrado; sem OpenTelemetry |
| **RNF-05** | Performance | Timeline sem scroll infinito real no frontend |
| **RNF-06** | Escalabilidade | MockGeocodingAdapter em produção |
| **RNF-07** | Acessibilidade | WCAG 2.1 AA não validado |
| **RNF-08** | Offline | Modo offline não implementado (RF-012) |
| **RNF-09** | I18n | Internacionalização não implementada (RF-014) |
| **RNF-10** | Segurança | E-mail de verificação não enviado após cadastro |

### 6.4 Débitos Técnicos

| ID | Camada | Descrição |
|---|---|---|
| **DT-01** | Backend | NotificationScheduler injetava JpaRepository externo (corrigido parcialmente) |
| **DT-03** | Backend | RSA key persistida programaticamente | ✅ Resolvido |
| **DT-04** | Frontend | PetSpecies enum duplicado/incompatível entre módulos | ✅ Resolvido |
| **DT-05** | Frontend | Estilos inline hardcoded em ProfilePage.tsx | ✅ Resolvido |
| **DT-06** | Frontend | VetFavoritesPage/VetProfilePage com CSS e lógica complexa inline | ✅ Resolvido |
| **DT-07** | Frontend | WeightRecordResponse em infra/dto mas consumido na application layer | ✅ Resolvido |
| **DT-09** | Backend | DOD menciona "Service" mas arquitetura é hexagonal |
| **DT-10** | Backend | Path inconsistente: `/auth/google` vs `/auth/oauth/google` | ✅ Resolvido |

---

## 7. Design System e UI/UX

### 7.1 Tokens de Design (theme.css)

O arquivo `apps/web/src/theme.css` define TODOS os tokens. **Nunca usar valores hardcoded.**

```css
/* Paleta Principal */
--color-primary: #9b4500;          /* Laranja-terra — identidade PetLife */
--color-secondary: #005fac;        /* Azul — ações secundárias */
--color-tertiary: #006b55;         /* Verde — sucesso/saúde */
--color-error: #ba1a1a;            /* Vermelho — erros */

/* Superfícies */
--color-surface: #f7f9fb;
--color-background: #f7f9fb;
--color-on-surface: #191c1e;

/* Tipografia */
--font-headline: 'Quicksand', sans-serif;
--font-body: 'Plus Jakarta Sans', sans-serif;

/* Espaçamento */
--space-xs: 8px; --space-sm: 16px; --space-md: 24px;
--space-lg: 40px; --space-xl: 64px;

/* Bordas */
--radius-sm: 0.25rem; --radius-md: 0.75rem;
--radius-lg: 1rem; --radius-full: 9999px;

/* Sombras */
--shadow-card: 0 4px 20px rgba(0, 0, 0, 0.06);
--shadow-modal: 0 8px 32px rgba(0, 0, 0, 0.12);
--shadow-button: 0 4px 12px rgba(155, 69, 0, 0.2);
```

### 7.2 Princípios UI/UX Obrigatórios

1. **Consistência Visual** — Sempre tokens semânticos do `theme.css`
2. **Micro-animações** — `transition: var(--transition-smooth)` em elementos interativos
3. **Feedback Visual** — Loading states, estados vazios e erros claros
4. **Acessibilidade** — Contraste mínimo 4.5:1, labels em todos os inputs, aria-*
5. **Mobile-First** — Breakpoint principal: 768px
6. **Dark Mode** — Suportado nativamente via tokens

### 7.3 Cores por Módulo (Timeline)

| Módulo | Token de Cor |
|---|---|
| Vacinas | `--color-tertiary` (verde) |
| Consultas | `--color-secondary` (azul) |
| Medicamentos | `--color-primary` (laranja) |
| Banho/Tosa | rosa (adicionar `--color-grooming`) |
| Fotos | amber (adicionar `--color-photo`) |
| Peso | `--color-tertiary-container` (teal) |

---

## 8. Módulos do Sistema

### Status de Implementação

| Módulo | ID | Backend | Frontend | Gap Crítico |
|---|---|---|---|---|
| Autenticação | M01 | 85% | 90% | Token refresh, Apple Sign-In, FCM |
| Gestão de Pets | M02 | 80% | 85% | DELETE /pets/{id}, link PDF |
| Vacinas e Vermífugos | M03 | 100% | 100% | N/A |
| Consultas | M04 | 100% | 100% | N/A |
| Medicamentos | M05 | 100% | 100% | N/A |
| Banho e Tosa | M06 | 100% | 100% | N/A |
| Linha do Tempo | M07 | 90% | 75% | Scroll infinito, link detalhe |
| Notificações | M08 | 100% | 100% | N/A |
| Veterinários | M09 | 100% | 100% | N/A |

### Endpoints de Referência
- **Swagger UI:** `http://localhost:8081/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8081/v3/api-docs`
- **Padrão:** `/api/v1/{recurso}`

---

## 9. Fluxos de Trabalho

> Workflows detalhados em `.gemini/workflows/`

### 9.1 Implementar Endpoint Faltante
1. Ler `docs/compliance-report.md` para identificar o gap
2. Ler PRD.md seção do módulo correspondente
3. Criar `XJpaEntity` + `XMapper` + `XPersistenceAdapter`
4. Adicionar método no Port (se novo)
5. Implementar Use Case + Controller + DTOs
6. Criar migration Flyway (se novo campo/tabela)
7. Escrever testes (JUnit + ArchUnit)
8. Executar gate check: `mvn clean compile` + greps do SDD

### 9.2 Criar Componente Frontend
1. Identificar nível no Atomic Design (atom/molecule/organism)
2. Criar pasta `components/{nivel}/{NomeComponente}/`
3. Criar `index.tsx` tipado + `styles.css` com tokens
4. Criar hook em `application/{modulo}/` se necessário
5. Criar adapter em `infrastructure/http/` se necessário
6. Escrever testes com RTL
7. Verificar: sem imports de `components/` em `application/`

### 9.3 Corrigir Débito Técnico
1. Identificar débito na Seção 6 deste GEMINI.md
2. Criar branch `fix/db-{ID}-descricao`
3. Implementar correção seguindo convenções da Seção 5
4. Executar gate checks (Seção 11)
5. Atualizar `docs/compliance-report.md`

---

## 10. Skills Disponíveis

| Skill | Arquivo SKILL.md | Descrição |
|---|---|---|
| `hexagonal-backend` | `.gemini/skills/hexagonal-backend/SKILL.md` | Implementar módulos com Arquitetura Hexagonal no backend Java |
| `atomic-frontend` | `.gemini/skills/atomic-frontend/SKILL.md` | Criar componentes React com Atomic Design + Vanilla CSS |
| `api-gap-fixer` | `.gemini/skills/api-gap-fixer/SKILL.md` | Identificar e implementar endpoints faltantes |
| `lgpd-compliance` | `.gemini/skills/lgpd-compliance/SKILL.md` | Verificar e implementar requisitos LGPD |
| `ux-design-petlife` | `.gemini/skills/ux-design-petlife/SKILL.md` | Design UI/UX com o design system PetLife |

---

## 11. Comandos Essenciais

### Backend
```bash
# Compilar — OBRIGATÓRIO após qualquer mudança
cd apps/backend && mvn clean compile

# Testes
mvn test

# Gate check arquitetural — DEVE retornar vazio
grep -rn "ApiResponse" src/main/java/com/petlife/modules/*/application/
grep -rn "JpaRepository\|JpaEntity" src/main/java/com/petlife/modules/*/application/
grep -rL "^package " src/main/java/ --include="*.java"

# Executar localmente (com Docker)
docker-compose up -d postgres redis rabbitmq minio
mvn spring-boot:run

# Swagger
open http://localhost:8081/swagger-ui.html
```

### Frontend
```bash
cd apps/web

# Desenvolvimento
pnpm dev

# Verificações — OBRIGATÓRIO antes de PR
pnpm run lint      # zero warnings
pnpm run typecheck # sem erros TypeScript

# Testes
pnpm test
pnpm test:e2e
pnpm test:coverage

# Build produção
pnpm build
```

### Docker
```bash
docker-compose up -d           # Subir todos os serviços
docker-compose logs -f backend # Ver logs
docker-compose down            # Parar tudo
```

---

## 12. Regras de Ouro para Agentes

### NUNCA FAÇA

1. **Nunca adicione `@Entity` em `domain/entity/`** — Crie `XJpaEntity` em `infrastructure/persistence/entity/`
2. **Nunca importe `Page<>` ou `Pageable` em Ports ou Use Cases** — Apenas em Adapters
3. **Nunca retorne `ApiResponse<>` em Use Cases** — Apenas em Controllers
4. **Nunca use cores/fontes/espaçamentos hardcoded no CSS** — Sempre `var(--token)`
5. **Nunca importe de `components/` dentro de `application/`** — Violação de DIP
6. **Nunca crie arquivo `.java` sem `package com.petlife...`**
7. **Nunca use Tailwind CSS** — O projeto usa Vanilla CSS
8. **Nunca commite sem `mvn clean compile` + `pnpm lint` passando**

### SEMPRE FAÇA

1. **Leia `docs/DOD.md`** antes de qualquer implementação
2. **Leia `docs/compliance-report.md`** para entender o estado atual
3. **Crie o Mapper ANTES do Adapter** (Regra R6 do SDD)
4. **Busque TODOS os call sites** ao renomear métodos de Port (Regra R3)
5. **Execute o gate check** da seção 4.4 do SDD-hexagonal-refactoring.md
6. **Use tokens semânticos** do `theme.css` em todo CSS novo
7. **Escreva testes** — Backend (JaCoCo >= 85%), Frontend (RTL + Vitest)
8. **Atualize `docs/compliance-report.md`** ao fechar um gap

### CHECKLIST DE PR

- [ ] `mvn clean compile` — sem erros
- [ ] `pnpm lint && pnpm typecheck` — sem erros
- [ ] `mvn test` passando — gate JaCoCo **≥ 85% de cobertura de linhas** (configurado no `pom.xml`)
- [ ] `pnpm test` passando — frontend (Vitest + RTL)
- [ ] Nenhum `@Entity` em `domain/entity/`
- [ ] Nenhum `ApiResponse<>` em Use Cases
- [ ] CSS usando apenas tokens de `theme.css`
- [ ] Nenhum import de `components/` em `application/`
- [ ] Migration Flyway criada (se alterou banco)
- [ ] `docs/compliance-report.md` atualizado (se fechou gap)
- [ ] Commit em Conventional Commits PT-BR

---

*Documento mantido pela equipe PetLife. Atualizar a cada sprint.*
*Para dúvidas: consultar `docs/adr/` ou abrir discussão no GitHub.*
