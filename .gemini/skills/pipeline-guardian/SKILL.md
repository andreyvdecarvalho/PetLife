---
name: pipeline-guardian
description: Ensure code passes CI gates (compilation, tests, and coverage >= 85%) before committing or pushing changes to prevent pipeline failures.
---

# 🛡️ Pipeline Guardian Skill

## Context
When an agent writes code or fixes bugs, the changes may inadvertently introduce regressions, compilation errors, or drop test coverage below the required threshold (85%). This leads to CI pipeline failures on GitHub Actions. To prevent this, the AI assistant must proactively act as a "Pipeline Guardian" and review all code changes using a strict gate check routine.

## Capabilities & Responsibilities
- Validate Backend logic by running `mvn clean test` and checking the JaCoCo report to ensure there are no compilation errors, `ApplicationContext` initialization failures, or test failures.
- Validate Frontend logic by running `pnpm test:coverage` and ensuring the coverage stays >= 85%.
- Validate code quality by running `pnpm lint` and `pnpm typecheck` in the frontend.

## Instructions
1. **Before any `git commit` or `git push`**, you MUST execute the verification steps below. DO NOT assume the code works just because the logic seems correct.
2. **Backend Validation**:
   - Run `mvn clean compile test` in `apps/backend`.
   - If tests fail, investigate the Surefire/JaCoCo reports. 
   - Pay special attention to Spring Context load failures (`IllegalState ApplicationContext failure threshold`). This often happens due to duplicate `@MockitoBean` definitions inherited from `IntegrationTestBase`.
   - Do NOT commit if tests are failing. Fix the tests first.
3. **Frontend Validation**:
   - Run `pnpm run lint` and `pnpm run typecheck` in `apps/web`.
   - Run `pnpm run test:coverage` in `apps/web`.
   - Ensure the total lines coverage is >= 85% (`cat coverage/coverage-summary.json | jq '.total.lines.pct'`). If it drops below 85%, write tests for the uncovered components before proceeding.
4. **Common Pitfalls to Review**:
   - **Duplicate Beans**: If you encounter duplicate `@MockitoBean` errors, verify if the test base class (`IntegrationTestBase`) already mocks the dependency.
   - **Path Mismatches**: If endpoints return `404` or `500` in tests, verify that the `@PostMapping`/`@GetMapping` paths exactly match the paths invoked by `mockMvc.perform()`.
   - **React Warnings**: Ensure `useEffect` dependencies are correctly wrapped and all hooks are exhaustive to pass ESLint without warnings.
