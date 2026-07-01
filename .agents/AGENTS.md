# PetLife — Regras de Agentes e Diretrizes de Projeto

Este documento contém as regras de desenvolvimento, restrições e diretrizes baseadas no PRD do projeto PetLife. Siga estas diretrizes ao escrever, refatorar ou sugerir código para este workspace.

## 0. Clean Architecture, SOLID e Arquitetura Hexagonal — Regras Fundamentais

> **LEIA ANTES DE ESCREVER QUALQUER LINHA DE CÓDIGO**
>
> O arquivo `skills/clean-architecture/SKILL.md` contém as regras completas de Clean Architecture, SOLID e Arquitetura Hexagonal para o PetLife.
> É a **fonte da verdade** para a estrutura de pacotes, camadas, Use Cases, Ports & Adapters e padrões de hook.

### 0.1 Princípios Não-Negociáveis

- **Arquitetura Hexagonal (Ports & Adapters)**: O core da aplicação (Domain + Application) deve ser completamente isolado das dependências externas (banco, controllers HTTP, mensageria, etc). O Core interage com o exterior apenas via **Ports** (interfaces de entrada/saída), e a infraestrutura implementa essas conexões através de **Adapters**.
- **SRP**: Use Cases com 1 responsabilidade. Hooks com 1 responsabilidade. Services legados com no máximo 150 linhas.
- **DIP**: Use Cases dependem de Ports (interfaces de domínio), nunca de JpaRepository ou adaptadores concretos. Hooks dependem de `authApi`/`petApi`, nunca de `axios` diretamente.
- **Camadas Backend**: `infrastructure (adapters) -> application (use cases / ports) -> domain (entities/exceptions)`. Nunca o inverso.
- **Camadas Frontend**: `components/pages (presentation) -> application (hooks) -> infrastructure (http/storage adapters) -> domain (entities)`.

### 0.2 Tamanho Máximo por Arquivo

| Tipo | Linhas Máximas |
|---|---|
| Use Case Java | 100 |
| Service Java (legado) | 150 |
| Controller Java | 80 |
| Context React | 80 |
| Hook React | 60 |
| Organism React | 120 |

### 0.3 Proibições Absolutas de Arquitetura

1. Lógica de negócio em Controller Java
2. JpaRepository diretamente em Use Case (usar Port)
3. localStorage direto em Context/Hook (usar tokenStorage)
4. window.location em interceptors de API
5. axios importado diretamente na camada application/domain
6. Import de CSS cross-component
7. Mock/stub de credenciais hardcoded em código de produção
8. Regex de validação duplicada (centralizar em validators.ts)
9. Tokens CSS inexistentes em theme.css

## 1. Design System — Referência Obrigatória

> **LEIA ANTES DE IMPLEMENTAR QUALQUER COMPONENTE VISUAL**
>
> O arquivo `DESIGN.md` contém o Design System completo extraído do protótipo Stitch (35 telas).
> É a **fonte da verdade** para todos os valores de design (cores, tipografia, espaçamentos, sombras, border-radius).

### 1.1 Regras de Fidelidade ao Design

- **Toda cor** usada no frontend DEVE corresponder a um token definido no `DESIGN.md` e mapeado em `src/theme.css`.
- **Nenhum valor hardcoded** é permitido para cores, fontes ou espaçamentos (exceto `#F1F5F9` para fundo de inputs, que é documentado como hardcoded no design original).
- **Ícones**: use exclusivamente Material Symbols Outlined. Os tamanhos documentados são: `text-[14px]` (badge), `text-[18px]` (chip), `20px` (inline), `24px` (padrão), `32px` (ilustrativo).
- **Tipografia**: fontes obrigatórias carregadas via Google Fonts — `Quicksand` (wght: 500,600,700) e `Plus Jakarta Sans` (wght: 400,500,600,700).

### 1.2 Mapa Rápido de Design Tokens -> CSS Custom Properties

| Valor no DESIGN.md | CSS Custom Property em theme.css |
|---|---|
| primary | --color-primary |
| surface-container-lowest | --color-surface-container-lowest |
| on-surface | --color-on-surface |
| error-container | --color-error-container |
| shadow-card | --shadow-card |
| radius-2xl | --radius-2xl |

### 1.3 Telas Priorizadas para Implementação

Ordem de implementação baseada no fluxo do usuário:
1. Login (/login) — concluído
2. Cadastro (/register) — concluído
3. Dashboard (/) — Stitch ID: 61b4a03107ab44c096db3aa6c02fc3c9
4. Perfil do Pet (/pets/:id) — Stitch ID: 974c612fe4c744adaf3aae75cc039666
5. Controle de Medicamentos (/medications) — Stitch ID: 2f0b52ee106f48c9b3f105ad561bdfb5
6. Gestão de Rotina (/routine) — Stitch ID: edd5938791e547a48fb6f99d91b9945c
7. Diário de Memórias (/memories) — Stitch ID: 165a4f285dc04599b1bd540e6bda0870
8. Agendamentos (/appointments) — Stitch ID: 7c874294a3da4a01b97b19dad8c121f4
9. Onboarding (4 telas) — começando em 57bb50529d4c4a9b8bb463c6b2661618

## 2. Stack Tecnológica

Mantenha a consistência com a seguinte stack tecnológica em todas as implementações:
- **Mobile**: React Native 0.76.0 (Versão fixa)
- **Web**: React 19.0.0 + Vite 6.0.0 (focado em PWA)
- **Gerenciador de Pacotes (Frontend)**: pnpm 10.34.4 (monorepo). **NUNCA** usar ranges (^, ~). Versões exatas obrigatórias.
- **Estilização**: Atomic Design com Vanilla CSS.
- **Backend**: Java 21 + Spring Boot 4.1.0 + Spring Security 6.4.0 + Spring Data JPA.
- **Build**: Maven 3.9.9.
- **Banco de Dados**: PostgreSQL 16.4.
- **ORM**: Hibernate 6.6.0 + Spring Data JPA. Migrations Flyway 10.20.0.
- **Cache**: Redis 7.4.0.
- **Armazenamento (Imagens/PDFs)**: AWS S3 ou MinIO.
- **Mensageria**: RabbitMQ 4.0.0.

> **Regra de Fixação de Versões:** NENHUM pacote deve utilizar atualizações automáticas, "latest" ou ranges de versão. **Todas as versões devem ser fixas**. Atualizações são estritamente manuais.

## 3. Padrões de API e Backend

- **Endpoints REST**: Todos os endpoints devem seguir o padrão `/api/v1/` e utilizar os verbos HTTP corretos.
- **Autenticação**: JWT (RS256) via Spring Security. Tokens de acesso com duração de 15 min e refresh tokens de 30 dias.
- **Senhas**: BCryptPasswordEncoder com strength 12.
- **Modelagem de Dados**:
  - Chaves primárias (id) devem ser UUID gerado automaticamente.
  - Entidades com campos de auditoria createdAt e updatedAt.
  - Exclusão em cascata para entidades dependentes de userId e petId (LGPD).
- **Validação**: Bean Validation (jakarta.validation) com @Valid nos controllers.
- **Tratamento de Erros**: @ControllerAdvice + @ExceptionHandler global.
- **Lógica de Negócio**: Nos Use Cases (application/usecase/), NUNCA em Controllers ou Repositories.

## 4. Segurança e LGPD

- Cascade delete rigoroso com CascadeType.REMOVE e orphanRemoval = true.
- Soft-delete com campo deletedAt quando exclusão física imediata não for obrigatória.
- Rate-limiting via Spring Cloud Gateway ou Bucket4j.
- **NUNCA** colocar credenciais, tokens mock ou dados sensíveis hardcoded em código de produção.

## 5. Frontend e UX

- **Idioma Padrão**: PT-BR obrigatório. Strings externalizadas em i18n.
- **Design Premium**: micro-interações, cores harmoniosas, usabilidade de primeira linha.
- **Acessibilidade**: WCAG 2.1 Nível AA. Inputs com erro DEVEM ter aria-invalid e aria-describedby.
- **Offline First**: Suporte a sincronização offline/online.

## 6. Regras de Código e Commits

- Java: camelCase para métodos/variáveis, PascalCase para classes.
- Frontend: TypeScript strict.
- Backend: SLF4J + Logback (JSON em produção).
- Javadoc em interfaces públicas.
- Lógica de negócio nos Use Cases, NUNCA em Controllers ou Repositories.
- **Commits**: Conventional Commits em PT-BR (ex: feat: adiciona login, fix: corrige validacao de email).

## 7. Estrutura de Pacotes Java (Clean Architecture)

```
com.petlife/
├── config/          <- Spring configs separados por responsabilidade
│   ├── SecurityConfig.java   <- Apenas SecurityFilterChain
│   ├── CorsConfig.java       <- Apenas CORS
│   ├── JwtConfig.java        <- Apenas JWT Encoder/Decoder
│   └── JacksonConfig.java
│
├── modules/
│   └── {modulo}/
│       ├── application/
│       │   ├── usecase/      <- Use Cases (SRP: 1 UC por operação)
│       │   └── port/         <- Ports/interfaces de domínio (DIP)
│       ├── domain/
│       │   ├── entity/       <- Entidades JPA
│       │   └── exception/    <- Exceções de domínio específicas
│       └── infrastructure/
│           ├── controller/   <- Controllers REST thin
│           ├── persistence/  <- Implementações dos Ports
│           └── dto/          <- Request/Response DTOs
│
├── shared/
│   ├── exception/   <- GlobalExceptionHandler, BusinessException
│   ├── security/    <- JwtService, UserPrincipal, RsaKeyConfig
│   └── response/    <- ApiResponse<T>, PageResponse<T>
│
└── PetLifeApplication.java
```

## 8. Estrutura Frontend (Clean Architecture + Atomic Design)

```
src/
├── domain/                    <- Entidades de domínio (interfaces TS puras)
│   ├── user/ (User.ts, UserPlan.ts)
│   └── pet/ (Pet.ts)
│
├── application/               <- Casos de uso (hooks React)
│   ├── auth/
│   │   ├── useLogin.ts
│   │   ├── useRegister.ts
│   │   ├── useLogout.ts
│   │   ├── useGoogleLogin.ts
│   │   └── useSession.ts
│   └── user/
│       ├── useUpdateProfile.ts
│       └── useDeleteAccount.ts
│
├── infrastructure/            <- Adaptadores de infraestrutura
│   ├── http/
│   │   ├── api.ts             <- Instância Axios (sem window.location)
│   │   └── auth.api.ts        <- Serviço HTTP de auth
│   └── storage/
│       └── tokenStorage.ts    <- Abstração de localStorage
│
├── contexts/
│   └── AuthContext.tsx        <- Thin: estado + composição de hooks
│
├── utils/
│   └── validators.ts          <- Validações centralizadas (DRY)
│
├── components/                <- Atomic Design
│   ├── atoms/
│   ├── molecules/
│   ├── organisms/
│   ├── templates/
│   └── pages/
│
├── pages/                     <- Thin wrappers de rota
└── theme.css
```

### 8.1 Regras de Organização do Frontend

- Subpastas: atoms/, molecules/, organisms/, templates/ e pages/.
- Cada componente: pasta própria com index.tsx + styles.css.
- Classes CSS únicas e semânticas (ex: .atom-button, .organism-login-form).
- Sem Tailwind, sem CSS Modules, sem CSS-in-JS.
- Apenas variáveis de src/theme.css para cores, fontes, espaçamentos.
- Atoms: width: 100%, sem margens externas fixas.
- Organisms e Templates ditam o grid (Flexbox ou CSS Grid).
