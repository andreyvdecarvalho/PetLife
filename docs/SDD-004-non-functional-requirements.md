# Spec Driven Development (SDD-004): Requisitos Não-Funcionais

> **Status:** 🟡 Pendente | **Prioridade:** P3 — Requisitos Não-Funcionais
> **Responsável:** Agente Implementador | **Sprint:** 5–6 (Semanas 5–6)
> **Débitos cobertos:** RNF-03, RNF-04, RNF-05, RNF-07, RNF-10

## Objetivo
Implementar melhorias não-funcionais de produção no PetLife:
- **RNF-10** Enviar e-mail de verificação após cadastro (Segurança).
- **RNF-04** Observabilidade: Integração com o Sentry no Backend e Frontend.
- **RNF-05** Timeline do pet com paginação e scroll infinito real (Performance).
- **RNF-07** Validar acessibilidade WCAG 2.1 AA com testes automatizados (Acessibilidade).
- **RNF-03** Configuração do Redis em produção como session store.

---

## 1. FASE 1 — RNF-10: E-mail de Verificação (4h)

### Contexto
Usuários cadastrados hoje entram no status `active` imediatamente sem verificar a validade do e-mail de cadastro.

### 1.1 Solução

#### [NEW] `apps/backend/src/main/resources/db/migration/V{N}__add_email_verification.sql`
```sql
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS email_verified BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS verification_token VARCHAR(255);
```

#### [NEW] `apps/backend/src/main/java/com/petlife/modules/auth/domain/port/EmailPort.java`
```java
package com.petlife.modules.auth.domain.port;

public interface EmailPort {
    void sendVerificationEmail(String email, String token);
}
```

#### [NEW] `apps/backend/src/main/java/com/petlife/modules/auth/infrastructure/adapter/EmailAdapter.java`
```java
package com.petlife.modules.auth.infrastructure.adapter;

import com.petlife.modules.auth.domain.port.EmailPort;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailAdapter implements EmailPort {

    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verifique sua conta PetLife");
        message.setText("Use o link a seguir para verificar sua conta: " +
            "https://petlife.com/verify?token=" + token);
        mailSender.send(message);
    }
}
```

---

## 2. FASE 2 — RNF-04: Integração Sentry (2h)

### Contexto
O projeto não possui rastreamento de erros centralizado na nuvem.

### 2.1 Backend

#### [MODIFY] `apps/backend/pom.xml`
Adicionar:
```xml
<dependency>
    <groupId>io.sentry</groupId>
    <artifactId>sentry-spring-boot-starter-jakarta</artifactId>
    <version>7.8.0</version>
</dependency>
```

#### [MODIFY] `apps/backend/src/main/resources/application.properties`
```properties
sentry.dsn=${SENTRY_DSN}
sentry.traces-sample-rate=0.1
```

### 2.2 Frontend

#### [MODIFY] `apps/web/package.json`
Adicionar dependency `@sentry/react`.

#### [MODIFY] `apps/web/src/main.tsx`
```typescript
import * as Sentry from "@sentry/react";

Sentry.init({
  dsn: import.meta.env.VITE_SENTRY_DSN,
  integrations: [
    Sentry.browserTracingIntegration(),
    Sentry.replayIntegration(),
  ],
  tracesSampleRate: 0.1,
});
```

---

## 3. FASE 3 — RNF-05: Scroll Infinito na Timeline (4h)

### Contexto
A API da timeline já possui paginação, mas o frontend exibe apenas a primeira página.

### 3.1 Solução

#### [MODIFY] `apps/web/src/application/pet/useTimeline.ts`
Implementar lógica para acumular páginas carregadas e buscar a próxima se houver:
```typescript
const [events, setEvents] = useState<TimelineEvent[]>([]);
const [page, setPage] = useState(0);
const [hasMore, setHasMore] = useState(true);

const loadMore = async () => {
  if (!hasMore) return;
  const result = await timelineApi.list(petId, page + 1);
  setEvents(prev => [...prev, ...result.content]);
  setPage(result.pageNumber);
  setHasMore(result.pageNumber < result.totalPages - 1);
};
```

#### [MODIFY] `apps/web/src/components/organisms/TimelineList/index.tsx`
Adicionar um elemento sensor (IntersectionObserver sentinel) no final da lista para chamar `loadMore` quando estiver visível.

---

## 4. FASE 4 — RNF-07: Testes WCAG 2.1 AA (6h)

### Contexto
Nenhuma validação de acessibilidade existe na pipeline de CI.

### 4.1 Solução
Integrar o `@axe-core/react` e `vitest-axe` nos testes unitários e de integração do frontend para auditar a legibilidade das cores, contraste e a presença de `aria-labels` em botões de ícone e controles de formulário.

---

## 5. FASE 5 — RNF-03: Session Store via Redis (2h)

### Contexto
Sessões de usuário do Spring Security e rate-limiting são armazenadas em memória local, o que impede a escalabilidade horizontal e faz o estado se perder a cada deploy.

### 5.1 Solução

#### [MODIFY] `apps/backend/src/main/resources/application.properties`
```properties
spring.session.store-type=redis
spring.data.redis.url=${REDIS_URL}
```

---

## 6. Gate Check — SDD-004
```bash
cd apps/backend && mvn clean compile && mvn test
cd apps/web && pnpm lint && pnpm typecheck && pnpm test
```

---

*SDD-004 criado pela Antigravity AI — 27/07/2026*
