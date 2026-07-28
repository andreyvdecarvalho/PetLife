# 🛡️ Pipeline Guardian Workflow

## Objective
Establish a standard workflow that all AI agents must follow before committing and pushing code, to ensure that the project compiles, all tests pass, coverage remains above 85%, and there are no linting or typechecking errors.

## Trigger
- When an AI agent modifies code and is preparing to perform `git commit` or `git push`.
- When an AI agent creates a new feature, fixes a bug, or handles technical debt.

## Required Skill
- `pipeline-guardian`

## Steps

### Step 1: Frontend Pre-commit Checks (Se houver alterações em apps/web)
1. **Linter e Typecheck:**
   - Execute `pnpm run lint` e `pnpm run typecheck` dentro de `apps/web`.
   - Resolva avisos do ESLint (como `react-hooks/exhaustive-deps`) imediatamente, sem usar pragmas como `// eslint-disable` a menos que seja estritamente necessário.
2. **Cobertura de Testes Frontend:**
   - Execute `pnpm run test:coverage`.
   - Se a taxa for inferior a 85%, identifique os arquivos com mais linhas descobertas (`coverage-summary.json`) e escreva novos casos de teste (`.spec.tsx`) focados em lógicas ou *branches* que não estavam cobertas.

### Step 2: Backend Pre-commit Checks (Se houver alterações em apps/backend)
1. **Compilação e Testes Integrados:**
   - Execute `mvn clean compile test` dentro de `apps/backend`.
   - Em caso de falha de carregamento de Contexto do Spring (`ApplicationContext failure`), inspecione o arquivo XML gerado pelo Surefire (em `target/surefire-reports`) para capturar a exceção raiz. 
   - A causa comum inclui `NoClassDefFoundError` ou erro de declaração duplicada de Beans em classes de teste (`@MockitoBean` redundante se herdado da `IntegrationTestBase`).
2. **Verificação Arquitetural:**
   - O projeto utiliza testes do `ArchUnit`. Garanta que todos passem (ex. Controllers retornando `ApiResponse`, sem injeção de JPA nos Casos de Uso, etc.).

### Step 3: Correção Ativa e Iterativa
1. Se qualquer teste (Frontend ou Backend) falhar, o agente NÃO deve prosseguir com o commit. 
2. A IA deve debugar o código, corrigir o mock ou o endpoint inconsistente (como chamadas a endpoints com ou sem `/oauth`), e então re-executar os testes da respectiva camada até obter 100% de sucesso.

### Step 4: Commit e Push
1. Apenas após a confirmação total de estabilidade (Compilação, Lint, Typecheck, Testes passando e Coverage >= 85%), efetue o `git commit` usando *Conventional Commits* em Português (ex. `fix(tests): ...`, `feat(auth): ...`).
2. Execute o `git push` para acionar a pipeline remota com confiança absoluta de que o código irá passar nos checks.
