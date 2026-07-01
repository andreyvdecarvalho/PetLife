---
name: tdd-red-green-refactor
description: >
  Workflow principal de TDD (Red → Green → Refactor) para o PetLife.
  Ativa quando o agente precisa implementar qualquer nova funcionalidade do zero
  seguindo o ciclo TDD completo. Garante que testes sejam escritos ANTES do código de produção.
  Palavras-chave: TDD, workflow, ciclo TDD, red green refactor, nova feature, nova funcionalidade, implementar do zero.
---

# Workflow: TDD Red → Green → Refactor — PetLife (Java)

## Quando Usar Este Workflow

Use este workflow **sempre** que:
- Implementar uma nova User Story do backlog
- Adicionar um novo endpoint REST ao backend Java
- Criar um novo componente de UI no frontend React
- Implementar uma regra de negócio nova no Service

---

## O Ciclo Obrigatório

```
📋 ANÁLISE → 🔴 RED → 🟢 GREEN → 🔵 REFACTOR → ✅ DONE
```

---

## Fase 0 — ANÁLISE (antes de qualquer código)

**Duração estimada:** 5–15 min

### Checklist
- [ ] Identificar a User Story (ex: `US-011`)
- [ ] Ler os Critérios de Aceitação do módulo correspondente no PRD
- [ ] Identificar **o que** será testado (comportamento, não implementação)
- [ ] Decidir o tipo de teste:
  - Regra de negócio pura → **Unit test** com Mockito (sem Spring)
  - Endpoint HTTP → **Integration test** com `@SpringBootTest` + MockMvc + Testcontainers
  - Fluxo de usuário completo → **E2E** com Playwright
- [ ] Mapear cenários de teste:
  - ✅ Caminho feliz (happy path)
  - ❌ Casos de erro (not found, validation, auth, limite de plano)
  - ⚠️ Edge cases (campos opcionais, limites de plano Free vs Premium)

---

## Fase 1 — 🔴 RED (Escreva o Teste Falhando)

**Regra de ouro:** Nenhuma linha de código de produção antes de ter um teste falhando.

### Checklist
- [ ] Criar a classe de teste no diretório `src/test/java/...`
- [ ] Escrever o `@DisplayName` descritivo em PT-BR
- [ ] Escrever todos os `@Test` para os cenários (podem estar vazios com `fail("not implemented")`)
- [ ] Rodar `mvn test -pl apps/backend` → confirmar que falha com `ClassNotFoundException` ou similar
- [ ] Adicionar asserções reais no primeiro `@Test`
- [ ] Rodar novamente → confirmar falha com mensagem semântica

### Saída esperada no RED
```
[ERROR] Tests run: 1, Failures: 0, Errors: 1
  VaccinationServiceTest > shouldCreateVaccinationAndReturnIt
    java.lang.ClassNotFoundException: com.petlife.modules.vaccination.VaccinationService
```

---

## Fase 2 — 🟢 GREEN (Escreva o Mínimo de Código)

**Regra:** Escreva o mínimo necessário para fazer o teste passar. Não antecipe.

### Checklist
- [ ] Criar a classe de produção (ex: `VaccinationService.java`)
- [ ] Implementar **apenas** o que o teste exige
- [ ] NÃO adicionar funcionalidades extras
- [ ] Rodar `mvn test` → confirmar que o teste passa (🟢)
- [ ] Garantir que testes anteriores continuam passando

### Saída esperada no GREEN
```
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
  ✓ VaccinationServiceTest > shouldCreateVaccinationAndReturnIt (45ms)
```

### Repita para cada `@Test`
1. Escreva o próximo teste (RED)
2. Faça passar (GREEN)
3. Continue até todos os cenários estarem cobertos

---

## Fase 3 — 🔵 REFACTOR (Melhore sem Quebrar)

**Regra:** Só refatore quando **todos** os testes estão verdes. Nenhum teste novo aqui.

### Checklist
- [ ] Extrair lógica duplicada em métodos privados ou classes utilitárias
- [ ] Melhorar nomenclatura seguindo convenções Java (camelCase, nomes semânticos)
- [ ] Remover código morto
- [ ] Garantir que toda regra de negócio está no **Service** (não no Controller ou Repository)
- [ ] Avaliar queries JPA (evitar N+1, usar `@EntityGraph` quando necessário)
- [ ] Adicionar `@Slf4j` e logs estruturados para rastreabilidade
- [ ] Rodar `mvn verify` → confirmar cobertura e gates passando

---

## Fase 4 — ✅ DONE (Critérios de Conclusão)

### Checklist Final (Backend Java)
- [ ] `mvn test` — todos os unit tests passando
- [ ] `mvn verify` — todos os integration tests passando + JaCoCo gate ≥ 80%
- [ ] `mvn checkstyle:check` — sem violations
- [ ] `mvn spotbugs:check` — sem bugs HIGH
- [ ] Critérios de aceitação do PRD cobertos por testes
- [ ] Javadoc nos métodos públicos do Service e Controller
- [ ] PR description: quais testes foram adicionados e cenários cobertos
- [ ] OpenAPI (`@Operation`) atualizado no Controller

### Checklist Final (Frontend React)
- [ ] `npx vitest run --coverage` — cobertura ≥ 75%
- [ ] `npx tsc --noEmit` — sem erros TypeScript
- [ ] `npx eslint src --max-warnings 0` — sem warnings
- [ ] Teste de acessibilidade `jest-axe` passando
- [ ] Teste E2E Playwright para o fluxo principal

---

## Exemplo Completo: US-011 (Registrar Vacina)

### Critérios de Aceitação (PRD M03)
- Tutor registra vacina com nome, data e veterinário
- Sistema calcula `next_dose_date` automaticamente
- Notificação push 7 dias antes e no dia D

### Fase 0: Análise
- **Tipo backend:** Unit (VaccinationService) + Integration (VaccinationController)
- **Cenários:**
  1. ✅ Criar vacina com dados válidos → retorna `VaccinationResponse` com `id`
  2. ✅ Calcular `nextDoseDate` automaticamente para V8/V10 (anual)
  3. ❌ Lançar `PET_NOT_FOUND` (404) se pet não pertence ao usuário autenticado
  4. ❌ Lançar `VALIDATION_ERROR` (422) se `dateAdministered` é data futura
  5. ✅ `reminderActive` deve ser `true` por padrão
  6. ✅ Enfileirar mensagem RabbitMQ após criação bem-sucedida

### Fase 1: RED

```java
@ExtendWith(MockitoExtension.class)
class VaccinationServiceTest {

    @InjectMocks VaccinationService vaccinationService;
    @Mock VaccinationRepository vaccinationRepository;
    @Mock PetService petService;
    @Mock NotificationProducer notificationProducer;

    @Test
    @DisplayName("Deve criar vacina e retornar com id gerado")
    void shouldCreateVaccinationAndReturnId() { fail("not implemented"); }

    @Test
    @DisplayName("Deve calcular nextDoseDate automaticamente para vacinas anuais")
    void shouldCalculateNextDoseDateForAnnualVaccines() { fail("not implemented"); }

    @Test
    @DisplayName("Deve lançar PET_NOT_FOUND se pet não pertence ao usuário")
    void shouldThrowWhenPetDoesNotBelongToUser() { fail("not implemented"); }

    @Test
    @DisplayName("Deve lançar VALIDATION_ERROR se data de aplicação é futura")
    void shouldThrowWhenDateAdministeredIsInFuture() { fail("not implemented"); }

    @Test
    @DisplayName("reminderActive deve ser true por padrão")
    void shouldSetReminderActiveTrueByDefault() { fail("not implemented"); }

    @Test
    @DisplayName("Deve enfileirar notificação após criar vacina")
    void shouldEnqueueNotificationAfterCreate() { fail("not implemented"); }
}
```

### Fases 2 e 3
```
→ Implementar VaccinationService.java (GREEN)
→ Refatorar: extrair calculateNextDoseDate() em VaccinationHelper
→ Refatorar: centralizar enqueue de notificação
→ Implementar VaccinationController.java + DTO
→ Escrever VaccinationControllerIT.java (integration test)
```

---

## Comandos Rápidos

```bash
# Rodar APENAS unit tests (rápido, sem Docker)
mvn test -pl apps/backend

# Rodar unit + integration tests (com Testcontainers/Docker)
mvn verify -pl apps/backend

# Rodar um único arquivo de teste
mvn test -pl apps/backend -Dtest=VaccinationServiceTest

# Gerar relatório de cobertura JaCoCo
mvn verify -pl apps/backend && open apps/backend/target/site/jacoco/index.html

# Checkstyle
mvn checkstyle:check -pl apps/backend

# Frontend: rodar em modo watch
cd apps/web && npx vitest

# Frontend: cobertura
cd apps/web && npx vitest run --coverage

# E2E
cd apps/web && npx playwright test
```
