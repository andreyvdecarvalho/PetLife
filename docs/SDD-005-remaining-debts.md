# Spec Driven Development (SDD-005): Débitos Remanescentes

> **Status:** 🟡 Pendente | **Prioridade:** P2
> **Responsável:** Agente Implementador | **Sprint:** 7 (Semana 7)
> **Débitos cobertos:** DB-06, RNF-02, RNF-06, RNF-08, RNF-09, DT-01

## Objetivo
Resolver as pendências funcionais, não-funcionais e técnicas restantes mapeadas no backlog do PetLife, deixando o sistema robusto, escalável e dentro dos padrões de Clean Architecture.

## 1. FASE 1 — DB-06: Apple Sign-In (4h)
### Contexto
O fluxo de login social previu Google e Apple, mas apenas Google foi finalizado.
### Solução
- **Backend**: Implementar `AppleLoginUseCase` e validação do token JWT via JKWS oficial da Apple.
- **Frontend**: Habilitar o botão de Apple Sign-In na UI e implementar o fluxo OAuth correspondente.

## 2. FASE 2 — RNF-02: JWT Tokens com Configuração Flexível (2h)
### Contexto
Algumas configurações de expiração e secrets dos tokens JWT estão *hardcoded* ou pouco maleáveis.
### Solução
- **Backend**: Extrair as configurações para o `application.yml` (`jwt.access-token.expiration`, `jwt.refresh-token.expiration`) e injetar via `@Value` ou `@ConfigurationProperties`.

## 3. FASE 3 — RNF-06: Substituição do MockGeocodingAdapter (3h)
### Contexto
Atualmente, a aplicação usa `MockGeocodingAdapter` em produção.
### Solução
- **Backend**: Criar um `GoogleMapsGeocodingAdapter` (ou OpenStreetMapAdapter) implementando a porta `GeocodingPort` e usar profiles (`@Profile("!test")`) para ativá-lo em produção.

## 4. FASE 4 — RNF-08: Suporte a Modo Offline PWA (6h)
### Contexto
Requisito RF-012 não atendido: O web app não possui modo offline real.
### Solução
- **Frontend**: Instalar e configurar o `vite-plugin-pwa`.
- **Frontend**: Configurar estratégias de cache (Stale-while-revalidate) para chamadas de API de leitura (ex: listar pets) e *background sync* para operações de escrita.

## 5. FASE 5 — RNF-09: Internacionalização - I18n (4h)
### Contexto
Requisito RF-014 não atendido: O aplicativo suporta apenas português.
### Solução
- **Frontend**: Integrar `i18next` e `react-i18next`.
- Criar arquivos de tradução (PT-BR, EN-US e ES) e abstrair strings visuais.

## 6. FASE 6 — DT-01: Correção do NotificationScheduler (2h)
### Contexto
`NotificationScheduler` injeta e usa o `JpaRepository` diretamente, burlando as regras de Clean Architecture.
### Solução
- **Backend**: Criar um ou mais Use Cases (`GetPendingNotificationsUseCase`, `ProcessNotificationUseCase`) no domínio e atualizar o Scheduler para utilizá-los, mantendo o Core agnóstico a banco de dados.

---
*SDD-005 criado pela Antigravity AI — 28/07/2026*
