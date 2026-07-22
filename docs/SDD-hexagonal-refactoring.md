# Spec Driven Development (SDD): Refatoração Arquitetural Hexagonal

## Objetivo
Implementar as correções arquiteturais mapeadas no `hexagonal_refactoring_plan.md`, alinhando todo o projeto PetLife (Backend e Frontend) aos princípios rigorosos de Arquitetura Hexagonal (Ports & Adapters) definidos na skill de Clean Architecture.

## 1. Escopo de Atuação (Backend)

### 1.1 FASE 1: Módulo `auth`
- [x] Mover classes de `entity/`, `dto/` e `controller/` para os pacotes corretos de `domain` e `infrastructure`.
- [x] **TDD:** Escrever testes de mapeamento para `UserMapper`.
- [x] Criar entidade JPA (`UserJpaEntity`) isolada da entidade de domínio (`User`).
- [x] Criar `UserRepositoryAdapter` que implemente `UserRepositoryPort` sem acoplar os use cases ao `JpaRepository`.
- [x] Remover acoplamentos diretos (`MultipartFile`, S3, JWT) dos Use Cases criando abstrações (Ports).
- [x] **ArchUnit:** Criar `HexagonalArchitectureTest` no pacote `auth` garantindo ausência de dependências do Spring no Core.

### 1.2 FASE 2: Módulo `pet`
- [x] Ajustar pacotes do domínio.
- [x] Limpar as 6 entidades principais removendo anotações `@Entity` e movendo para a camada de infraestrutura as implementações JPA.
- [x] Criar `PetPersistenceAdapter` e `RoutineActivityPersistenceAdapter`.
- [x] **TDD:** Testar novos adapters e garantir que comportamentos transacionais permanecem corretos.
- [x] Extrair paginação Spring Data (`Pageable`, `Page`) das interfaces de Port.
- [x] Extrair criação de PDF e uploads S3 dos Use Cases.

### 1.3 FASE 3: Módulos `medication` e `notification`
- [x] Isolar repositórios JPA no módulo `medication`.
      → Use Cases usam apenas `petId`/`petOwnerId`/`medicationId`/`medicationName` (campos planos do domínio).
      → `MedicationMapper` e `MedicationAdministrationMapper` preenchem esses campos na conversão JPA→domínio.
- [x] Corrigir `NotificationScheduler` que consome infraestrutura de módulos vizinhos diretamente.
      → Substituído `MedicationAdministrationJpaRepository` pelo `MedicationAdministrationRepositoryPort`.
      → Novo método `findByStatusAndScheduledTimeBefore` adicionado ao Port e implementado no Adapter.
- [x] Corrigir vazamento HTTP (`ApiResponse`) em Use Cases.
      → `GetNotificationsUseCase` retorna `PagedResult<NotificationResponse>` (tipo de domínio).
      → `NotificationController` constrói o `ApiResponse.paged()` a partir do `PagedResult`.
- [x] Remover campo `petEntity` (referência JPA) da entidade de domínio `Medication` — mantém apenas `petId` + `petOwnerId`.
- [x] Criar método Port para consulta de administrações pendentes por data (`findByStatusAndScheduledTimeBefore`).
- [x] Corrigir `NotificationMessageJpaRepository` (package ausente, imports de `Page`/`Pageable` faltando, tipo de retorno da query paginada usando domínio em vez de JPA entity).
- [x] Criar `NotificationPreferencesMapper` (estava ausente, causando falha de compilação no adapter).
- [x] Corrigir `NotificationPreferencesRepositoryAdapter` (import de `NotificationPreferences` ausente).
- [x] Extrair `PagedResult` record para arquivo próprio (`PagedResult.java`) — Java exige que tipos `public` top-level fiquem em arquivo com o mesmo nome.
- [x] Corrigir `GetPetTimelineUseCase` (`findByPetEntityId` → `findByPetId` após renomeação no Port da Fase 2).
- [x] Remover `Page<T>` e `Pageable` do Spring Data do `NotificationMessageRepositoryPort` (violação R5).
      → Port agora recebe `int page, int size` e retorna `PagedResult<NotificationMessage>` (tipo de domínio puro).
      → `NotificationMessageRepositoryAdapter` constrói `PageRequest` internamente e converte `Page<JpaEntity>` → `PagedResult<Domain>`.
      → `GetNotificationsUseCase` livre de qualquer import `org.springframework.data.domain.*`.
      → `NotificationController` extrai `page`/`size` via `@RequestParam` e repassa ao Use Case.

### 1.4 FASE 4: Módulo `veterinarian` e Shared Config
- [ ] Restaurar pacote `domain/entity` no módulo Veterinarian.
- [ ] Corrigir `MockGeocodingAdapter`.
- [ ] Refatorar a geração efêmera de par de chaves RSA (`RsaKeyConfig`).

## 2. Escopo de Atuação (Frontend)

### 2.1 Adapters e Hooks
- [ ] **TDD:** Criar testes para os novos arquivos `veterinarian.api.ts` e `routine.api.ts`.
- [ ] Substituir acesso direto de `api` (axios) dentro dos hooks de `veterinarian` e `routine`.
- [ ] Corrigir inversão de DIP em `usePushNotifications` injetando o hook `useToast` ou callback similar, evitando importação de componente da camada de Presentation.
- [ ] Mover manipulação direta de DOM do `useExportMedicalPass` para uma camada de infra (BrowserAdapter).

## 3. Diretrizes de Execução TDD (Red-Green-Refactor)
Para cada refatoração:
1. **Red:** Escreva ou ajuste os testes (ex: ArchUnit ou testes de UseCase desacoplados) garantindo que falham (ou mapeie testes existentes que quebrarão).
2. **Green:** Crie as entidades de infra (JpaEntity), mapeadores (Mapper) e adaptadores (PersistenceAdapter) até o projeto compilar e testes passarem.
3. **Refactor:** Elimine código legado (imports antigos, repositórios estendendo Port diretamente) e refine Use Cases.

---

## 4. Regras Preventivas de Refatoração (Lições Aprendidas)

> **Estas regras foram extraídas dos erros encontrados nas Fases 1–3.**
> O agente **DEVE** verificar cada item antes de marcar qualquer tarefa como concluída.

### 4.1 Regras de Compilação Java

| # | Regra | Erro que previne |
|---|---|---|
| R1 | **Cada tipo `public` top-level em arquivo próprio.** Records, classes e interfaces `public` não podem compartilhar arquivo com outro tipo `public`. | `class X is public, should be declared in a file named X.java` |
| R2 | **Toda classe/interface/record Java DEVE ter declaração `package` na primeira linha não-comentada.** Arquivos sem `package` são colocados no pacote padrão e causam falhas crípticas. | `bad source file: file does not contain class X` |
| R3 | **Ao renomear um método em um Port, buscar TODOS os call sites antes de finalizar.** Usar: `grep -r "nomeAntigo" src/main/java`. | `cannot find symbol: method findByPetEntityId(UUID)` |
| R4 | **JpaRepository deve parametrizar com `XJpaEntity`, nunca com entidade de domínio.** Queries paginadas retornam `Page<XJpaEntity>`; o Adapter converte para domínio. | Erros de import de `Pageable` em cascata mascarando a causa raiz |
| R5 | **Imports de `Page` e `Pageable` pertencem a `org.springframework.data.domain.*`** e devem estar nos Adapters e JpaRepositories, NUNCA em Ports ou Use Cases. | Cascata de erros de compilação obscurecendo o erro raiz |
| R11 | **Ports de paginação devem usar `int page, int size` e retornar `PagedResult<T>` de domínio.** O Adapter constrói `PageRequest` internamente e é o único detentor de `Page<JpaEntity>`. O Controller extrai `page`/`size` via `@RequestParam` simples — sem `@PageableDefault` ou `Pageable` do Spring MVC. | Violação de R5 no Port; acoplamento do Use Case ao Spring Data |

### 4.2 Regras de Criação de Adapters e Mappers

| # | Regra | Erro que previne |
|---|---|---|
| R6 | **Criar o Mapper ANTES do Adapter.** Todo Adapter que mapeia domínio↔JPA depende do Mapper; sem ele o Adapter não compila. | `cannot find symbol: class XMapper` |
| R7 | **Verificar que todos os métodos do Port estão implementados no Adapter com `@Override`.** O compilador Java só detecta ausência de métodos se o Adapter tiver `implements XPort`. | Métodos faltando descobertos apenas em runtime |
| R8 | **Ao "achatar" campos JPA em campos planos de domínio** (ex: `petEntity → petId + petOwnerId`), atualizar TODOS os pontos de uma vez: (1) entidade de domínio, (2) mapper, (3) todos os Use Cases, (4) todos os `mapToResponse`. | `NullPointerException` em runtime ou erros de compilação espalhados |

### 4.3 Regras de Módulos Cruzados

| # | Regra | Erro que previne |
|---|---|---|
| R9 | **Nunca injetar `XJpaRepository` de um módulo A dentro de qualquer `@Component` do módulo B.** Criar Port em A e injetá-lo em B. | Acoplamento cross-módulo quebrando o isolamento hexagonal |
| R10 | **`ApiResponse<T>` é exclusivo da camada Controller.** Use Cases retornam tipos simples de domínio (records, listas, objetos planos). | Vazamento da camada HTTP no Application Core |

### 4.4 Checklist de Gate — Executar após CADA fase

Antes de marcar qualquer item como `[x]`, validar com esses comandos:

```bash
# 1. Compilação limpa obrigatória (sem cache de bytecode anterior)
mvn clean compile

# 2. Métodos renomeados: nenhum call site usando o nome antigo
grep -rn "findByPetEntityId\|getPetEntity()\|getMedication()\." \
  src/main/java/com/petlife/modules/

# 3. ApiResponse em Use Cases (deve retornar vazio)
grep -rn "ApiResponse" \
  src/main/java/com/petlife/modules/*/application/

# 4. JpaRepository ou JpaEntity em camada application (deve retornar vazio)
grep -rn "JpaRepository\|JpaEntity" \
  src/main/java/com/petlife/modules/*/application/

# 5. Arquivos Java sem declaração de package (deve retornar vazio)
grep -rL "^package " src/main/java/ --include="*.java"

# 6. Tipos public em arquivo de nome diferente
# (rodar no bash; PowerShell users: adaptar com Get-Content)
grep -rn "^public record \|^public class \|^public interface " \
  src/main/java/ --include="*.java" \
  | while IFS=: read file line content; do
      type_name=$(echo "$content" | grep -oP '(?<=public (record|class|interface) )\w+')
      file_name=$(basename "$file" .java)
      if [ "$type_name" != "" ] && [ "$type_name" != "$file_name" ]; then
        echo "MISMATCH: $file → tipo '$type_name' deve estar em $type_name.java"
      fi
    done
```

---

## 5. Próximos Passos
O Agente Implementador (`tdd-backend-implementer` e `tdd-frontend-implementer`) deve pegar os itens do **Escopo de Atuação** e executar módulo a módulo de maneira isolada. Nenhuma PR será considerada completa sem:

1. `mvn clean compile` passando com **zero erros**.
2. Todos os comandos grep da **Seção 4.4** retornando **sem ocorrências**.
3. Validação via **ArchUnit** garantindo imunidade do Domínio contra o Spring.
