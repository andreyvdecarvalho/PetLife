# Spec Driven Development (SDD-005): Débitos Remanescentes

> **Status:** 🟡 Pendente | **Prioridade:** P2
> **Responsável:** Agente Implementador | **Sprint:** 7 (Semana 7)
> **Débitos cobertos:** DB-06, RNF-02, RNF-06, RNF-08, RNF-09, DT-01

## Objetivo
Resolver as pendências funcionais, não-funcionais e técnicas restantes mapeadas no backlog do PetLife, deixando o sistema robusto, escalável e dentro dos padrões de Clean Architecture.

### Fase 1: Finalizar Apple Sign-In (DB-06)
**Status**: [X] Concluído

1. **Backend**:
   - Criar `AppleOAuthPort` em `domain/port`.
   - Implementar `AppleOAuthAdapter` validando JWT via chaves públicas da Apple (`https://appleid.apple.com/auth/keys`) ou via Nimbus JOSE + JWT.
   - Criar `AppleLoginUseCase` injetando porta do Apple e de User.
   - Adicionar endpoint `POST /auth/oauth/apple` no `AuthController`.

2. **Frontend**:
   - Implementar `useAppleLogin` hook.
   - Adicionar botão de "Continuar com Apple" na UI do Login e Register.
   - Integrar no fluxo de OAuth do AuthContext.

---

### Fase 2: Flexibilização dos JWT Tokens (RNF-02)
**Status**: [X] Concluído

- Mover configurações *hardcoded* de tempo de expiração do `JwtService` e de Refresh Token para variáveis no `application.yml`.
- Referenciar variáveis de ambiente: `JWT_ACCESS_TOKEN_EXPIRATION_MINUTES`, `JWT_REFRESH_TOKEN_EXPIRATION_DAYS`.

---

### Fase 3: Substituição do MockGeocodingAdapter em Produção (RNF-06)
**Status**: [X] Concluído

- Adicionar anotação `@Profile("test")` no atual `MockGeocodingAdapter`.
- Criar `OpenStreetMapGeocodingAdapter` ou `GoogleMapsGeocodingAdapter` (com anotação `@Profile("!test")`).
- Implementar chamada real de API de Geocoding (ex: Nominatim do OSM não exige apiKey para requests de baixo volume).

---

### Fase 4: Modo Offline da PWA (RNF-08)
**Status**: [X] Concluído

- Adicionar dependência `vite-plugin-pwa` no frontend.
- Configurar Service Worker no `vite.config.ts` com estratégias de cache `NetworkFirst` (API) e `CacheFirst` (assets estáticos).

---

### Fase 5: Internacionalização / I18n (RNF-09)
**Status**: [X] Concluído

- Adicionar `i18next` e `react-i18next`.
- Prover *dictionaries* básicos para pt-BR, en-US e es-ES (foco primário em fluxo de Auth e NavBar).
- Criar Hook `useLanguage` ou plugar no Context.

---

### Fase 6: Correção do NotificationScheduler (DT-01)
**Status**: [X] Concluído

- Criar `ProcessPendingEventsUseCase` no domínio de *Notification*.
- Orquestrar a injeção das portas do Domínio de Pet, Vaccination e Medication diretamente nesse UseCase em vez de injetá-las no Scheduler (camada de infraestrutura).
- Alterar o `NotificationScheduler` para chamar `processPendingEventsUseCase.checkUpcomingEvents()` e etc.

---
*SDD-005 criado pela Antigravity AI — 28/07/2026*
