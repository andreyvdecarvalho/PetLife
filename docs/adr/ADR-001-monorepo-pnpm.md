# ADR 001: Arquitetura de Monorepo com PNPM Workspaces

## Status
Aprovado

## Data
30 de Junho de 2026

## Contexto
O projeto PetLife é um ecossistema composto por múltiplas aplicações: uma aplicação frontend web (React + Vite), uma aplicação mobile (React Native) e uma API backend (Spring Boot). Gerenciar múltiplos repositórios separados aumenta a complexidade de compartilhamento de código (como tipos TypeScript, componentes em comum, tokens do design system) e dificulta a manutenção de pipelines de CI/CD centralizados e o rastreamento de tarefas.

Além disso, a segurança da cadeia de suprimentos (supply chain) é uma preocupação crítica. Pacotes recém-lançados no ecossistema npm podem conter vulnerabilidades intencionais ou ataques de *dependency confusion* / *typosquatting*.

## Decisão
Decidimos estruturar o repositório como um **Monorepo** utilizando o **pnpm** com suporte a **Workspaces** (`pnpm-workspace.yaml`).

As aplicações ficam localizadas sob o diretório `/apps`:
- `apps/web`: Frontend Web (React 19 + Vite 6).
- `apps/mobile`: Frontend Mobile (React Native 0.76.0).
- `apps/backend`: API Backend (Java 21 + Spring Boot 4.1.0).

Pacotes compartilhados internos de frontend ficarão sob `/packages` (ex: design tokens, tipos comuns).

Configuramos o arquivo `.npmrc` na raiz para impor regras estritas de segurança:
1. Bloqueio de instalação de qualquer pacote com idade inferior a 10 dias (14400 minutos) usando a diretiva `minimum-release-age=14400` para mitigar ataques de dia zero.
2. Fixação obrigatória de versões exatas de dependências no `package.json` (sem caracteres de range como `^` ou `~`).

## Consequências
### Positivas:
- Facilidade de compartilhamento de código e tipos TypeScript entre Web e Mobile.
- Builds locais mais rápidos devido ao cache inteligente do `pnpm`.
- Centralização de scripts e tarefas comuns na raiz do projeto.
- Maior segurança contra vulnerabilidades em dependências npm de terceiros recém-publicadas.

### Negativas / Custos:
- Curva de aprendizado inicial para gerenciar dependências no escopo de workspaces.
- Necessidade de gerenciar e atualizar manualmente dependências, visto que o uso de ranges (`^`, `~`) está proibido.
- A restrição de idade mínima de dependências pode atrasar a adoção imediata de novas versões de pacotes (embora mitigada via exclusões pontuais autorizadas pelo tech lead no `pnpm-workspace.yaml`).
