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
- [ ] Ajustar pacotes do domínio.
- [ ] Limpar as 6 entidades principais removendo anotações `@Entity` e movendo para a camada de infraestrutura as implementações JPA.
- [ ] Criar `PetPersistenceAdapter` e `RoutineActivityPersistenceAdapter`.
- [ ] **TDD:** Testar novos adapters e garantir que comportamentos transacionais permanecem corretos.
- [ ] Extrair paginação Spring Data (`Pageable`, `Page`) das interfaces de Port.
- [ ] Extrair criação de PDF e uploads S3 dos Use Cases.

### 1.3 FASE 3: Módulos `medication` e `notification`
- [ ] Isolar repositórios JPA no módulo `medication`.
- [ ] Corrigir `NotificationScheduler` que consome infraestrutura de módulos vizinhos diretamente.
- [ ] Corrigir vazamento HTTP (`ApiResponse`) em Use Cases.

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

## 4. Próximos Passos
O Agente Implementador (`tdd-backend-implementer` e `tdd-frontend-implementer`) deve pegar os itens do **Escopo de Atuação** e executar módulo a módulo de maneira isolada. Nenhuma PR será considerada completa sem validação via ArchUnit (no backend) garantindo a imunidade do Domínio contra o Spring.
