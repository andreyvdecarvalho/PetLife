---
name: clean-architecture
description: >
  Skill de Clean Architecture e princípios SOLID para o PetLife.
  Ativa quando o agente precisa estruturar módulos, criar Use Cases, definir Ports &
  Adapters, refatorar Services com múltiplas responsabilidades, ou revisar separação
  de camadas no backend Java ou frontend React/TypeScript.
  Palavras-chave: Clean Architecture, SOLID, Use Case, Port, Adapter, SRP, OCP, DIP,
  camadas, domínio, aplicação, infraestrutura, refatoração, responsabilidade única.
---

# Skill: Clean Architecture e Arquitetura Hexagonal — PetLife

## Visão Geral

O PetLife adota **Clean Architecture** e **Arquitetura Hexagonal (Ports & Adapters)** em ambas as camadas (backend Java e frontend TypeScript).
Todo o código deve respeitar a separação e fluxo de dependências: **Infraestrutura (Adapters) → Aplicação (Use Cases & Ports) → Domínio (Entities & Exceptions)**.
Nunca o sentido inverso. O Core da aplicação é isolado e expõe apenas contratos de entrada/saída (Ports), enquanto os detalhes de entrega, banco de dados e drivers ficam em adaptadores na periferia do Hexágono.

---

## Princípios SOLID Obrigatórios

### S — Single Responsibility (Responsabilidade Única)
- Cada classe Java ou hook/componente React possui **uma e apenas uma razão para mudar**
- Use Cases do backend: **1 caso de uso = 1 arquivo** (ex: `RegisterUserUseCase.java`)
- Hooks do frontend: **1 hook = 1 responsabilidade** (ex: `useLogin.ts`)
- Evitar "God Classes" — Services com mais de 5 métodos públicos são candidatos a decomposição

### O — Open/Closed (Aberto/Fechado)
- Novas funcionalidades via **novos Use Cases**, não modificando existentes
- No frontend, novos comportamentos via **novos hooks**, não expandindo `AuthContext`
- Componentes UI com variantes via prop `variant`, não via condicionais inline

### L — Liskov Substitution (Substituição de Liskov)
- Toda implementação de Port (interface Java) deve ser **substituível** sem alterar o comportamento
- Mocks de teste devem implementar o mesmo Port que a implementação real

### I — Interface Segregation (Segregação de Interfaces)
- Ports Java: **um port por domínio de operação**
  - Ex: `UserRepositoryPort` (CRUD) separado de `PasswordResetTokenPort` (tokens)
- Hooks React: **um hook por operação**, não um mega-hook com tudo

### D — Dependency Inversion (Inversão de Dependência)
- Use Cases dependem de **interfaces (Ports)**, nunca de implementações concretas (JpaRepository)
- Hooks do frontend dependem de **serviços abstratos** (`authApi`), nunca de `axios` diretamente
- `localStorage`, `window.location` e outros objetos globais DEVEM ser encapsulados em módulos de infra

---

## Estrutura de Camadas — Backend Java

```
com.petlife.modules.{modulo}/
├── application/
│   ├── usecase/          ← Use Cases (1 por operação, SRP)
│   │   ├── Create{X}UseCase.java
│   │   ├── Update{X}UseCase.java
│   │   ├── Delete{X}UseCase.java
│   │   └── Get{X}UseCase.java
│   └── port/             ← Ports (interfaces de domínio, DIP)
│       └── {X}RepositoryPort.java
├── domain/
│   ├── entity/           ← Entidades JPA (@Entity)
│   └── exception/        ← Exceções de domínio específicas
└── infrastructure/
    ├── controller/       ← Controllers REST (thin, delegam ao Use Case)
    ├── persistence/      ← Implementações dos Ports (JPA)
    └── dto/              ← Request/Response DTOs
```

### Regras de Camada (Backend)
1. **Controllers** são THIN: recebem request, chamam Use Case, retornam ApiResponse. Nenhuma lógica de negócio.
2. **Use Cases** contêm TODA a lógica de negócio. Lançam `BusinessException`.
3. **Use Cases** dependem de Ports (interfaces), NUNCA de JpaRepository diretamente.
4. **Entities** não possuem lógica de negócio (somente mapeamento JPA + getters/setters Lombok).
5. **DTOs** ficam em `infrastructure/dto/` ou `dto/` no monólito — são específicos da camada HTTP.

### Padrão de Use Case

```java
// RegisterUserUseCase.java
@Component
@RequiredArgsConstructor
@Slf4j
public class RegisterUserUseCase {

    private final UserRepositoryPort userRepository; // Port, não JPA diretamente
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public TokenResponse execute(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw BusinessException.conflict("AUTH_EMAIL_ALREADY_EXISTS", "E-mail já cadastrado.");
        }
        // ...
    }
}
```

### Padrão de Port

```java
// UserRepositoryPort.java (na camada application/port/)
public interface UserRepositoryPort {
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    User save(User user);
    void delete(User user);
}

// UserJpaRepository.java (na camada infrastructure/persistence/)
@Repository
public interface UserJpaRepository extends JpaRepository<User, UUID>, UserRepositoryPort {
    // Spring Data JPA implementa automaticamente os métodos do Port
}
```

### Padrão de Controller Thin

```java
// AuthController.java
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    // Use Cases injetados diretamente — SRP: cada endpoint usa seu Use Case
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.of(registerUserUseCase.execute(request));
    }
}
```

---

## Estrutura de Camadas — Frontend TypeScript

```
src/
├── domain/                    ← Entidades de domínio puras (interfaces TS)
│   ├── user/
│   │   ├── User.ts
│   │   └── UserPlan.ts
│   └── pet/
│       └── Pet.ts
│
├── application/               ← Casos de uso (custom hooks React)
│   ├── auth/
│   │   ├── useLogin.ts        ← SRP: apenas login
│   │   ├── useRegister.ts     ← SRP: apenas registro
│   │   ├── useLogout.ts       ← SRP: apenas logout
│   │   ├── useGoogleLogin.ts  ← SRP: apenas Google OAuth
│   │   └── useSession.ts      ← SRP: apenas reidratação de sessão
│   └── user/
│       ├── useUpdateProfile.ts
│       └── useDeleteAccount.ts
│
├── infrastructure/            ← Adaptadores de infraestrutura
│   ├── http/
│   │   ├── api.ts             ← Instância Axios base
│   │   └── auth.api.ts        ← Serviço HTTP de auth (SRP)
│   └── storage/
│       └── tokenStorage.ts    ← Encapsula localStorage (DIP)
│
├── contexts/
│   └── AuthContext.tsx        ← Apenas estado global + composição de hooks
│
├── utils/
│   └── validators.ts          ← Validações centralizadas (DRY)
│
├── components/                ← Atomic Design
├── pages/
└── theme.css
```

### Regras de Camada (Frontend)
1. **Contextos** são THIN: gerenciam estado global e compõem hooks de aplicação.
2. **Hooks de aplicação** (`application/`) contêm a lógica de uso (orquestram serviços de infra).
3. **Serviços de infraestrutura** (`infrastructure/`) encapsulam HTTP, localStorage, etc.
4. **`localStorage` NUNCA é acessado diretamente** nos hooks ou contextos — sempre via `tokenStorage`.
5. **`axios` NUNCA é importado diretamente** nas camadas de aplicação — sempre via serviço HTTP.
6. **`window.location`** NUNCA é chamado dentro de interceptors — usar callbacks ou eventos customizados.

### Padrão de Token Storage

```typescript
// src/infrastructure/storage/tokenStorage.ts
const ACCESS_TOKEN_KEY = '@PetLife:accessToken';
const REFRESH_TOKEN_KEY = '@PetLife:refreshToken';

export const tokenStorage = {
  getAccessToken: (): string | null => localStorage.getItem(ACCESS_TOKEN_KEY),
  getRefreshToken: (): string | null => localStorage.getItem(REFRESH_TOKEN_KEY),
  setTokens: (accessToken: string, refreshToken: string): void => {
    localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
  },
  clearTokens: (): void => {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
  },
};
```

### Padrão de Serviço HTTP

```typescript
// src/infrastructure/http/auth.api.ts
import api from './api';

export const authApi = {
  login: (email: string, password: string) =>
    api.post<{ data: TokenResponse }>('/auth/login', { email, password }),

  register: (name: string, email: string, password: string) =>
    api.post<{ data: TokenResponse }>('/auth/register', { name, email, password }),

  getProfile: () =>
    api.get<{ data: UserResponse }>('/auth/me'),

  updateProfile: (data: Partial<UserResponse>) =>
    api.put<{ data: UserResponse }>('/auth/me', data),

  deleteAccount: () =>
    api.delete('/auth/me'),
};
```

---

## Checklist de Revisão de Clean Architecture

Antes de criar ou revisar qualquer código, verifique:

### Backend
- [ ] O Service/Use Case tem mais de uma responsabilidade? → Dividir em Use Cases
- [ ] O Use Case depende de JpaRepository diretamente? → Criar Port (interface)
- [ ] O Controller tem lógica de negócio? → Mover para Use Case
- [ ] A Entity tem métodos de negócio? → Mover para Use Case
- [ ] Configs estão separadas por responsabilidade? → `SecurityConfig`, `CorsConfig`, `JwtConfig`

### Frontend
- [ ] O Context tem lógica de HTTP/storage? → Extrair para hooks + infra
- [ ] O componente valida formulário? → Extrair para hook `use{Form}Form`
- [ ] O serviço usa `localStorage` diretamente? → Usar `tokenStorage`
- [ ] O interceptor usa `window.location`? → Usar callback injetado
- [ ] A regex de validação está duplicada? → Centralizar em `validators.ts`
- [ ] Os tokens CSS existem em `theme.css`? → Validar antes de usar

---

## Tamanho Máximo por Arquivo

| Tipo | Linhas Máximas | Ação se Exceder |
|---|---|---|
| Use Case Java | 100 | Extrair métodos privados ou criar helper |
| Service Java (legado) | 150 | Dividir em Use Cases |
| Controller Java | 80 | Controller está gordo → lógica no Use Case |
| Context React | 80 | Extrair hooks para `application/` |
| Hook React | 60 | Dividir em hooks menores |
| Organism React | 120 | Extrair lógica para hook `use{Organism}` |

---

## Proibições Absolutas

1. ❌ **Nunca** chamar `JpaRepository` diretamente em Use Case — usar Port
2. ❌ **Nunca** lógica de negócio em Controller Java
3. ❌ **Nunca** `localStorage` diretamente em Context/Hook — usar `tokenStorage`
4. ❌ **Nunca** `window.location` em interceptors de API
5. ❌ **Nunca** `axios` importado diretamente na camada de aplicação/domínio
6. ❌ **Nunca** `api.ts` genérico chamado em organismos React — usar serviço específico (`authApi`, `petApi`)
7. ❌ **Nunca** tokens CSS hardcoded ou tokens inexistentes em `theme.css`
8. ❌ **Nunca** import de CSS cross-component (ex: ForgotPasswordForm não pode importar LoginForm/styles.css)
9. ❌ **Nunca** mock/stub de credenciais ou tokens hardcoded em código de produção
10. ❌ **Nunca** regex de validação duplicada — centralizar em `src/utils/validators.ts`
