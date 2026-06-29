# PetLife — Regras de Agentes e Diretrizes de Projeto

Este documento contém as regras de desenvolvimento, restrições e diretrizes baseadas no PRD do projeto PetLife. Siga estas diretrizes ao escrever, refatorar ou sugerir código para este workspace.

## 1. Stack Tecnológica
Mantenha a consistência com a seguinte stack tecnológica em todas as implementações:
- **Mobile**: React Native 0.76.0 (Versão fixa)
- **Web**: React 19.0.0 + Vite 6.0.0 (focado em PWA)
- **Gerenciador de Pacotes (Frontend)**: pnpm 10.34.4 (monorepo). **NUNCA** usar ranges (`^`, `~`). Versões exatas obrigatórias.
- **Estilização**: Atomic Design com Vanilla CSS.
- **Backend**: Java 21 + Spring Boot 4.1.0 + Spring Security 6.4.0 + Spring Data JPA.
- **Build**: Maven 3.9.9.
- **Banco de Dados**: PostgreSQL 16.4.
- **ORM**: Hibernate 6.6.0 + Spring Data JPA. Migrations Flyway 10.20.0.
- **Cache**: Redis 7.4.0.
- **Armazenamento (Imagens/PDFs)**: AWS S3 ou MinIO.
- **Mensageria**: RabbitMQ 4.0.0.

> **Regra de Fixação de Versões:** NENHUM pacote, framework ou banco de dados deve utilizar atualizações automáticas, "latest" ou ranges de versão (ex: `^1.2.0`, `~1.2.0`, `1.2.x`). **Todas as versões devem ser fixas** em todo o código (ex: `1.2.0`). Atualizações são estritamente manuais.

## 2. Padrões de API e Backend
- **Endpoints REST**: Todos os endpoints devem seguir o padrão `/api/v1/` e utilizar os verbos HTTP corretos (GET, POST, PUT, PATCH, DELETE).
- **Autenticação**: Uso de JWT (RS256) via Spring Security. Tokens de acesso com duração de 15 min e refresh tokens de 30 dias.
- **Senhas**: Hashes gerados obrigatoriamente com `BCryptPasswordEncoder` e strength 12.
- **Modelagem de Dados**:
  - Chaves primárias (`id`) devem ser do tipo `UUID` gerado automaticamente (`@GeneratedValue` com estratégia `UUID`).
  - Entidades principais devem possuir campos de auditoria `createdAt` e `updatedAt` (via `@CreationTimestamp` e `@UpdateTimestamp`).
  - Exclusão em cascata deve ser configurada para entidades dependentes de `userId` e `petId` (para compliance com a LGPD na exclusão de contas).
- **Validação**: Use Bean Validation (`jakarta.validation`) nas entidades e DTOs com `@Valid` nos controllers.
- **Tratamento de Erros**: Use `@ControllerAdvice` + `@ExceptionHandler` para um handler global de erros com formato JSON padronizado.

## 3. Segurança e LGPD
- Respeite o direito do usuário à exclusão permanente dos seus dados e dos dados dos pets (`cascade delete` rigoroso com `CascadeType.REMOVE` e `orphanRemoval = true`).
- Implemente suporte nativo a soft-delete (ex: estado `ARCHIVED` ou campo `deletedAt`) quando a exclusão física imediata não for obrigatória.
- Proteja as rotas contra vulnerabilidades comuns (Top 10 OWASP) e trate rate-limiting (via Spring Cloud Gateway ou Bucket4j).

## 4. Frontend e UX
- **Idioma Padrão**: A interface do usuário é toda em Português do Brasil (PT-BR). Strings devem ser externalizadas em arquivos de i18n para suporte a múltiplos idiomas no futuro.
- **Design Premium**: Aplique princípios de design modernos, utilizando micro-interações, cores harmoniosas e garantindo usabilidade de primeira linha.
- **Acessibilidade**: Obedeça aos requisitos da WCAG 2.1 Nível AA (tamanho mínimo de fonte 14px, contraste correto).
- **Offline First**: A arquitetura deve prever suporte a sincronização de dados offline/online.
## 5. Regras de Código e Commits
- Todo código Java deve seguir as convenções de nomenclatura Java (camelCase para métodos/variáveis, PascalCase para classes).
- Frontend (React/React Native) deve ser estritamente tipado com TypeScript.
- Utilize logs estruturados no backend com **SLF4J + Logback** (formato JSON em produção via `logstash-logback-encoder`).
- Documente interfaces públicas com **Javadoc**, métodos críticos e fluxos complexos com comentários relevantes.
- Toda lógica de negócio deve residir na camada **Service**, nunca em Controllers ou Repositories.
- **Commits**: Utilize sempre o padrão **Conventional Commits** escrito em **Português do Brasil (PT-BR)** (ex: `feat: adiciona login`, `fix: corrige validacao de email`).

## 6. Estrutura de Pacotes Java (Padrão do Projeto)
```
com.petlife/
├── config/          ← Spring configs (Security, CORS, Jackson, etc.)
├── modules/
│   ├── auth/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   └── dto/
│   ├── pet/
│   ├── vaccination/
│   ├── consultation/
│   ├── medication/
│   ├── grooming/
│   ├── timeline/
│   └── notification/
├── shared/
│   ├── exception/   ← GlobalExceptionHandler, AppException
│   ├── security/    ← JwtFilter, JwtService
│   └── response/    ← ApiResponse<T>, PageResponse<T>
└── PetLifeApplication.java
```
