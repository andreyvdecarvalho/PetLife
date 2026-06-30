# Definition of Done (DoD) — PetLife

Este documento define os critérios de qualidade obrigatórios que qualquer funcionalidade (User Story ou Task) deve atender antes de ser considerada concluída e pronta para produção.

---

## 1. Código e Padrões de Projeto

### 1.1 Backend (Java / Spring Boot)
- [ ] O código segue rigidamente a convenção de nomenclatura Java (camelCase para métodos/variáveis, PascalCase para classes).
- [ ] Toda a lógica de negócio reside na camada **Service**, nunca em Controllers ou Repositories.
- [ ] Chaves primárias de novas tabelas/entidades utilizam obrigatoriamente `UUID` v4 gerado automaticamente.
- [ ] Entidades principais implementam auditoria (`createdAt` e `updatedAt`) via `@CreationTimestamp` e `@UpdateTimestamp`.
- [ ] Parâmetros de API e DTOs de entrada utilizam validações do `jakarta.validation` (`@NotNull`, `@NotBlank`, `@Size`, etc.) e são anotados com `@Valid` nos controllers.
- [ ] Novos endpoints REST seguem o padrão `/api/v1/` e utilizam os verbos HTTP corretos (GET, POST, PUT, PATCH, DELETE).

### 1.2 Frontend (React / TypeScript)
- [ ] O código é estritamente tipado com TypeScript (evitar o uso de `any`).
- [ ] A arquitetura segue rigidamente a organização do **Atomic Design** (`src/components/atoms/`, `src/components/molecules/`, etc.).
- [ ] Cada componente visual possui sua própria pasta contendo o componente (`index.tsx`) e seu estilo correspondente (`styles.css`).
- [ ] Estilos utilizam exclusivamente **Vanilla CSS** com seletores aninhados nativos. Frameworks como Tailwind, CSS Modules ou CSS-in-JS não são permitidos.
- [ ] Não há cores, fontes ou espaçamentos fixos no CSS. Todos os estilos consomem obrigatoriamente as variáveis semânticas expostas in `src/theme.css`.
- [ ] Componentes do tipo `Atoms` ocupam 100% de largura (`width: 100%`) e não definem margens externas fixas (`margin`).

---

## 2. Testes e Qualidade

- [ ] **Backend (Cobertura)**: O build do Maven passou pelo gate do JaCoCo com cobertura mínima de **85% de linhas** de código.
- [ ] **Frontend (Testes)**: Componentes novos ou modificados possuem testes unitários/integração usando React Testing Library e Jest.
- [ ] **Linter & Compilação**:
  - Backend compila sem warnings com `mvn clean compile`.
  - Frontend passa livre de erros no linter (`pnpm run lint`) e no typecheck (`pnpm run typecheck`).
- [ ] **Migrations**: Qualquer alteração no banco de dados possui uma migração correspondente do Flyway em `src/main/resources/db/migration/` devidamente testada.

---

## 3. Segurança e LGPD

- [ ] **Controle de Acesso**: Rotas que expõem dados sensíveis exigem token JWT (RS256) válido através de configuração no Spring Security.
- [ ] **LGPD Compliance**:
  - Exclusão de contas e pets implementa `cascade delete` rigoroso (`CascadeType.REMOVE` e `orphanRemoval = true`) para apagar fisicamente todos os dados vinculados ao ID do usuário em conformidade com o Artigo 18 da LGPD.
  - Campos que armazenam dados pessoais sensíveis contam com controle estrito de acesso.

---

## 4. Entrega e Versionamento

- [ ] As mensagens de commit seguem a especificação **Conventional Commits** em **Português do Brasil (PT-BR)** (ex: `feat: adiciona login`, `fix: corrige validacao de email`).
- [ ] O código foi integrado ao branch principal (`main`) através de um Pull Request aprovado.
- [ ] Toda a documentação pública correspondente (Javadoc, Swagger OpenAPI via SpringDoc em `http://localhost:8080/docs`) foi atualizada.
