# 🏗️ PetLife — Plano de Reestruturação: Clean Architecture, SOLID e Arquitetura Hexagonal

## Diagnóstico das Violações

### Backend

#### ❌ `AuthService.java` (246 linhas) — Violações Críticas
| Princípio | Violação |
|---|---|
| **SRP** | 7 responsabilidades distintas (register, login, google login, forgotPassword, resetPassword, updateProfile, deleteAccount) em uma única classe |
| **OCP** | Adicionar novo método de auth exige modificar a mesma classe |
| **DIP** | `AuthService` depende diretamente de `JwtEncoder`, `JwtDecoder`, `ObjectMapper` — acoplamento de infra no domínio |
| **Clean Arch** | Sem camada de Use Cases — lógica de negócio misturada com orquestração e infra |

#### ❌ `SecurityConfig.java` (93 linhas)
| Princípio | Violação |
|---|---|
| **SRP** | CORS config, JWT config, PasswordEncoder e SecurityFilterChain em uma classe |

#### ❌ Ausência de Camadas
- ❌ Sem **Use Cases** (`application/usecases/`)
- ❌ Sem **Ports** (interfaces de infra)
- ❌ Sem **Mappers** dedicados

### Frontend

#### ❌ `AuthContext.tsx` (159 linhas) — Violações Críticas
| Princípio | Violação |
|---|---|
| **SRP** | State management + HTTP calls + localStorage + session rehydration numa classe |
| **DIP** | Chama `api` diretamente — sem abstração do serviço HTTP |
| **Clean Arch** | Context mistura Application Layer com Infrastructure Layer |

#### ❌ `src/services/api.ts` (mínimo)
- Serviço genérico único — sem separação por domínio

---

## Nova Estrutura — Backend (Clean Architecture)

```
com.petlife/
├── config/              ← Spring configs
│   ├── SecurityConfig.java
│   ├── CorsConfig.java       ← EXTRAÍDO de SecurityConfig
│   ├── JwtConfig.java        ← EXTRAÍDO de SecurityConfig
│   └── JacksonConfig.java
│
├── modules/
│   └── auth/
│       ├── application/
│       │   ├── usecase/      ← NOVO: Use Cases (SRP por UC)
│       │   │   ├── RegisterUserUseCase.java
│       │   │   ├── LoginUserUseCase.java
│       │   │   ├── LoginWithGoogleUseCase.java
│       │   │   ├── ForgotPasswordUseCase.java
│       │   │   ├── ResetPasswordUseCase.java
│       │   │   ├── GetUserProfileUseCase.java
│       │   │   ├── UpdateUserProfileUseCase.java
│       │   │   └── DeleteUserAccountUseCase.java
│       │   └── port/         ← NOVO: Ports (DIP)
│       │       ├── UserRepository.java (interface)
│       │       └── PasswordResetTokenPort.java (interface)
│       ├── domain/
│       │   ├── entity/       ← User.java, UserPlan.java
│       │   └── exception/    ← AuthExceptions específicas
│       ├── infrastructure/
│       │   ├── controller/   ← AuthController.java (thin)
│       │   ├── persistence/  ← UserJpaRepository.java (impl do Port)
│       │   └── dto/          ← Request/Response DTOs
│       └── AuthService.java  ← REMOVIDO → dividido em Use Cases
│
└── shared/
    ├── exception/
    ├── response/
    └── security/
        ├── JwtService.java
        ├── UserPrincipal.java
        ├── RsaKeyConfig.java
        └── JwtFilter.java
```

## Nova Estrutura — Frontend (Clean Architecture)

```
src/
├── domain/                    ← NOVO: Entidades de domínio
│   ├── user/
│   │   ├── User.ts            ← interface User
│   │   └── UserPlan.ts
│   └── pet/
│       └── Pet.ts
│
├── application/               ← NOVO: Casos de uso (hooks)
│   ├── auth/
│   │   ├── useLogin.ts        ← hook de login
│   │   ├── useRegister.ts     ← hook de cadastro
│   │   ├── useLogout.ts       ← hook de logout
│   │   ├── useGoogleLogin.ts  ← hook de Google OAuth
│   │   └── useSession.ts      ← hook de sessão/reidratação
│   └── user/
│       ├── useProfile.ts
│       ├── useUpdateProfile.ts
│       └── useDeleteAccount.ts
│
├── infrastructure/            ← NOVO: Adaptadores de infra
│   ├── http/
│   │   ├── api.ts             ← instância Axios
│   │   └── auth.api.ts        ← NOVO: serviço HTTP de auth
│   └── storage/
│       └── tokenStorage.ts    ← NOVO: abstração localStorage
│
├── contexts/
│   └── AuthContext.tsx        ← SIMPLIFICADO: apenas state + hooks
│
├── components/                ← Atomic Design (mantido)
├── pages/
└── theme.css
```

---

## Princípios SOLID Applied

### S — Single Responsibility
- Backend: cada Use Case tem uma responsabilidade
- Frontend: cada hook tem uma responsabilidade
- `SecurityConfig` dividida em `SecurityConfig` + `CorsConfig` + `JwtConfig`

### O — Open/Closed
- Backend: novos fluxos de auth via novos Use Cases, sem modificar existentes
- Frontend: novos fluxos via novos hooks

### L — Liskov Substitution
- Ports (interfaces Java) substituíveis por qualquer implementação
- TypeScript: interfaces de serviço substituíveis por mocks em testes

### I — Interface Segregation
- `UserRepository` separado de `PasswordResetTokenPort`
- Hooks específicos em vez de um context mega

### D — Dependency Inversion
- Use Cases dependem de interfaces (Ports), não de JPA repositories diretamente
- Frontend: hooks dependem de `tokenStorage` e `auth.api`, não de `localStorage` e `api` diretamente
