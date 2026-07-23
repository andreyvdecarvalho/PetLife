# Plano de Refatoração Arquitetural (PetLife)
**Foco:** Arquitetura Hexagonal (Ports & Adapters) e Clean Architecture

Este documento consolida as auditorias de todos os módulos do PetLife e define um plano de ação passo a passo para erradicar o acoplamento com frameworks (Spring, Hibernate, JPA) no Core (Domínio/Aplicação) do Backend, e violações de DIP no Frontend.

---

## 🛑 Resumo Executivo das Violações Sistêmicas

1. **JPA no Domínio (Nível 1 - Crítico):** Quase todas as entidades de domínio possuem anotações `@Entity`, `@Table`, e dependências diretas do Hibernate.
2. **Falsa Inversão de Dependência (Nível 1 - Crítico):** Os `Ports` de Repositório estão sendo implementados DIRETAMENTE pelas interfaces do Spring Data JPA (`extends JpaRepository, Port`), sem um `Adapter` real atuando como barreira arquitetural.
3. **Vazamento de Infraestrutura para o Domínio/Aplicação (Nível 1 e 2):** 
   - Tipos do Spring Data (`Page`, `Pageable`) nas interfaces dos Ports.
   - Tipos Spring MVC (`MultipartFile`) nos Use Cases.
   - Use Cases retornando envelopamentos HTTP (`ApiResponse`).
   - Use Cases importando DTOs da camada `infrastructure`.
   - Gerenciamento de transações (`@Transactional`) nos Use Cases ao invés dos Adapters.
4. **Violações no Frontend:** Inexistência de Adapters HTTP dedicados para `routine` e `veterinarian`, resultando em hooks da camada Application acessando a instância `api` (Axios) diretamente.

---

## 🛠️ Plano de Execução: Backend (Ordem Recomendada)

Recomenda-se executar a refatoração módulo a módulo para evitar "Big Bang". O módulo `auth` deve ser o primeiro, pois é fundacional e menos complexo.

### FASE 1: Fundações e Módulo `auth` (Prioridade Máxima)

1. **Correção de Estrutura de Pacotes:**
   - Mover `modules/auth/entity/*` para `modules/auth/domain/entity/`.
   - Mover `modules/auth/dto/*` para `modules/auth/infrastructure/dto/`.
   - Mover `modules/auth/controller/*` para `modules/auth/infrastructure/controller/`.
2. **Limpeza do Domínio `User` (Nível 1):**
   - Criar `UserJpaEntity` em `infrastructure/persistence/entity/` com as anotações `@Entity`.
   - Limpar o `domain/entity/User`, transformando-o num POJO puro (sem jakarta.persistence).
   - Criar `UserMapper` em `infrastructure/persistence/mapper/`.
3. **Criação do Adapter Real (Nível 1):**
   - Remover `extends UserRepositoryPort` de `UserJpaRepository`.
   - Remover o arquivo órfão `repository/UserRepository.java`.
   - Criar `UserRepositoryAdapter implements UserRepositoryPort` que injeta o `UserJpaRepository` e usa o `UserMapper`.
4. **Desacoplamento dos Use Cases (Nível 2):**
   - Extrair lógicas de infra de `UploadUserPhotoUseCase` para um `StoragePort` (remover S3 hardcoded e `MultipartFile`).
   - Mover decodificação do JWT Google em `LoginWithGoogleUseCase` para um `GoogleAuthPort`.
   - Substituir `PasswordEncoder` e `JwtEncoder/Decoder` nos Use Cases por `PasswordHashPort` e `TokenPort`.
   - Mover DTOs para fora dos Use Cases (usar objetos Command/Result puros da aplicação).
   - *Opcional:* Remover `@Transactional` e `@Component` dos Use Cases, movendo a transação para os Adapters.

### FASE 2: Módulo `pet` (Maior volume de arquivos)

1. **Estruturação do Domínio:**
   - Mover `modules/pet/entity/` para `modules/pet/domain/entity/`.
2. **Limpeza de Domínio (Nível 1):**
   - Separar entidades JPA de `Pet`, `Consultation`, `Grooming`, `Vaccination`, `WeightRecord` e `RoutineActivity`. Criar POJOs puros para o domínio.
   - Mover `JsonListStringConverter` para `infrastructure/persistence/converter/`.
3. **Adapters Reais (Nível 1):**
   - Criar `PetPersistenceAdapter` implementando `PetRepositoryPort` (remover o `extends` no `PetJpaRepository`).
   - Criar `RoutineActivityPersistenceAdapter`.
4. **Limpeza de Ports:**
   - Substituir `Page` e `Pageable` no `PetRepositoryPort` por abstrações de paginação de domínio (ou Listas).
5. **Correção de Use Cases (Nível 2):**
   - Eliminar `PdfWriter`/`iText` de `ExportPetMedicalPassUseCase` delegando para um `PdfExportPort`.
   - Remover `MultipartFile` e S3 hardcoded de todos os `Upload*UseCase`.
   - Corrigir Use Cases de `RoutineActivity` que retornam entidades JPA diretamente.
   - Corrigir falha de segurança no `GetPetWeightHistoryUseCase` (adicionar verificação de ownership).
   - Padronizar todos os 32 Use Cases (Remover `@Slf4j`, `@Service/Component` se adotada Opção A, extrair DTOs).

### FASE 3: Módulos `medication` e `notification`

1. **Módulo Medication:**
   - Limpar `@Entity` de `Medication` e `MedicationAdministration`.
   - Criar `MedicationRepositoryAdapter` e `MedicationAdministrationRepositoryAdapter`.
   - Remover signature JPA `<S extends MedicationAdministration>` do `MedicationAdministrationRepositoryPort`.
   - Tratar Use Cases extensos (ex: `CreateMedicationUseCase` com 202 linhas) extraindo mapeamento DTO para a camada infra.
2. **Módulo Notification (CRÍTICO):**
   - **CORREÇÃO IMEDIATA (VIO-NOT-11):** O `NotificationScheduler` (infra) injeta repositórios JPA de OUTROS módulos diretamente. Substituir por chamadas a Ports/Use Cases.
   - **CORREÇÃO IMEDIATA (VIO-NOT-06):** O `GetNotificationsUseCase` retorna `ApiResponse<>` e `Page`. O Use Case NÃO PODE conhecer abstrações HTTP.
   - Limpar `@Entity` de `NotificationMessage` e `NotificationPreferences`.
   - Criar `NotificationCommand` no lugar de `NotificationPayload` no `NotificationPublisherPort`.

### FASE 4: Módulo `veterinarian` e Shared/Config

1. **Módulo Veterinarian:**
   - Mover a pasta `entity/` inteira (que está na raiz) para `domain/entity/`. Atualizar dezenas de imports quebrados em cascata.
   - Limpar `@Entity` de `Veterinarian`, `VetAddress`, `VetFavorite`, `VetSchedule`.
   - Limpar vazamento do Spring Data (`Page<>`, `SearchVeterinariansRequest` de DTO) do `VeterinarianRepositoryPort` e Adapters correspondentes.
   - Mover/Substituir `MockGeocodingAdapter` (Mock com dados hardcoded de produção não deve ficar em `infrastructure/adapter/`).
   - Abstrair `HttpStatus` das exceções de domínio.
2. **Shared / Config / Security:**
   - **CRÍTICO:** Atualizar `RsaKeyConfig` para que as chaves RSA não sejam geradas efemeramente em memória (o que desloga usuários a cada deploy).
   - Remover acoplamentos de `Shared` com módulos específicos (Ex: `UserPrincipal` importando `User` do módulo auth).
   - Adicionar handlers que faltam no `GlobalExceptionHandler`.

---

## 🌐 Plano de Execução: Frontend

1. **Adapters HTTP Ausentes (🔴 Crítico):**
   - Criar `src/infrastructure/http/veterinarian.api.ts` e refatorar `useVeterinarianProfile.ts`, `useVetFavorites.ts`, `useSearchVeterinarians.ts`, e `useGetVetProfile.ts` para não usar `axios/api` direto.
   - Criar `src/infrastructure/http/routine.api.ts` e refatorar `useRoutineActivities.ts`.
2. **Violações Inversas de DIP:**
   - Remover `import { useToast }` de `usePushNotifications.ts` (Application chamando Presentation). Injetar via callback.
   - Extrair manipulação DOM (`window.URL.createObjectURL`, `document.createElement`) de `useExportMedicalPass.ts` para um Adapter de Browser.
3. **Organização (Estrutural):**
   - Unificar enum duplicado/incompatível `PetSpecies` em `src/domain/shared/Species.ts`.
   - Criar interface em `src/domain/pet/RoutineActivity.ts` em vez de deixar tipos de domínio soltos no Hook de Application.
   - Mover `WeightRecordResponse` para a camada Domain (atualmente em infra/dto, mas consumida na application).
   - Limpar páginas (VetFavoritesPage, VetProfilePage) movendo CSS isolado e lógica UI complexa para components.
   - Mover estilos inline hardcoded de `ProfilePage.tsx` para CSS.

---

## 🔒 Implementando Garantias (ArchUnit)

Após a refatoração de cada módulo, travar a regressão implementando testes automatizados de arquitetura (no backend):

```java
@AnalyzeClasses(packages = "com.petlife.modules.auth")
class HexagonalArchitectureTest {
    
    // Nenhuma classe no domínio pode conhecer JPA ou Spring
    @ArchTest
    static final ArchRule domain_framework_agnostic =
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "jakarta.persistence..", "org.hibernate..", "org.springframework.."
            );

    // Use cases não dependem de Repositórios JPA
    @ArchTest
    static final ArchRule app_nao_depende_jpa =
        noClasses().that().resideInAPackage("..application..")
            .should().dependOnClassesThat().areAssignableTo(JpaRepository.class);
            
    // Use cases não importam DTOs da infra
    @ArchTest
    static final ArchRule app_nao_depende_dto_infra =
        noClasses().that().resideInAPackage("..application.usecase..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure.dto..");
}
```
