# Relatório de Setup Inicial: Projeto PetLife

Este documento registra as atividades realizadas durante a fase de setup inicial do projeto **PetLife**, garantindo que a base de código e as diretrizes de desenvolvimento estejam alinhadas com as definições do PRD e as melhores práticas de arquitetura e segurança.

## 1. Definição e Estruturação do Repositório
O repositório foi inicializado e estruturado como um **Monorepo** utilizando o `pnpm` workspace, permitindo o compartilhamento eficiente de código entre as aplicações web e mobile.

- **`package.json` (Root)**: Criado com scripts centralizados para rodar os ambientes de desenvolvimento (`dev:web`, `dev:mobile`), testes, linting e typecheck. A versão do `pnpm` (10.34.4) e do `node` (22.0.0) foram fixadas.
- **`pnpm-workspace.yaml`**: Configurado para mapear as pastas `apps/web`, `apps/mobile` e pacotes compartilhados em `packages/*`.
- **`.npmrc`**: Regras estritas de segurança adicionadas, como o bloqueio de instalação de pacotes recém-publicados (`minimum-release-age`), prevenindo ataques de supply chain (ex: *dependency confusion*).

## 2. Documentação de Requisitos e Diretrizes
Foram criados os documentos base para guiar o desenvolvimento do projeto tanto por desenvolvedores humanos quanto por agentes de IA:

- **`PRD.md`**: Adicionado o Documento de Requisitos do Produto, contendo o escopo completo, arquitetura, fluxos de usuário e requisitos não-funcionais.
- **`AGENTS.md`**: Definidas as regras estritas de desenvolvimento para o projeto, incluindo:
  - Fixação obrigatória de todas as versões da Stack (React Native 0.76.0, React 19.0.0, Spring Boot 3.4.0, PostgreSQL 16.4, etc.).
  - Padrões de API REST, segurança (JWT) e tratamento de erros.
  - Regras de conformidade com a LGPD (exclusão em cascata, soft-delete).
  - Estrutura de pacotes padronizada para o backend Java.

## 3. Configuração de Skills (Agentes IA)
Para garantir que a inteligência artificial auxilie no desenvolvimento seguindo os padrões corretos, foram criadas **Skills** específicas no diretório `.agents/skills/`:
- `api-design`: Foco em design de APIs REST com Spring Boot, DTOs e Bean Validation.
- `ci-cd-pipeline`: Setup de fluxos de CI/CD via GitHub Actions com gates de cobertura.
- `lgpd-compliance`: Diretrizes para garantir a privacidade, consentimento e exclusão de dados dos usuários.
- `notification-system`: Regras para o sistema de notificações push, mensageria e agendamentos.
- `schema-design`: Padrões para modelagem de banco de dados e uso do Flyway com Spring Data JPA.
- `tdd-backend` & `tdd-frontend`: Skills para garantir o desenvolvimento guiado por testes no backend (JUnit/Testcontainers) e no frontend (RTL/Playwright).
- `tdd-red-green-refactor`: Workflow obrigatório para a criação de novas funcionalidades a partir do zero.

## 4. Controle de Versão
- Todos os arquivos do setup foram adicionados ao controle de versão (`git`).
- Foi gerado o commit inicial: `chore: setup do projeto (regras, skills e seguranca da stack)`.
- O código foi submetido (pushed) com sucesso para o branch `main` do repositório remoto no GitHub (`origin/main`).

## Próximos Passos Sugeridos
Com a fundação do projeto estabelecida, as próximas fases podem focar em:
1. Inicialização do projeto **Backend** (Spring Boot) dentro da sua respectiva pasta, utilizando as configurações de banco e mensageria definidas.
2. Criação do boilerplate das aplicações **Web** (Vite + React) e **Mobile** (React Native) nas pastas `apps/web` e `apps/mobile`.
3. Configuração do pipeline inicial no GitHub Actions (`.github/workflows`).
