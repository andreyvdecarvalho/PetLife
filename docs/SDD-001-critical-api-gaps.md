# Spec Driven Development (SDD-001): Gaps Críticos de API

> **Status:** 🟢 Concluído | **Prioridade:** P0 — Bloqueadores de Produção
> **Responsável:** Agente Implementador | **Sprint:** 1 (Semanas 1–2)
> **Débitos cobertos:** DB-01, DB-02, DB-03, DB-04, DB-05, DB-07

## Objetivo
Implementar os 6 débitos críticos que bloqueiam o uso em produção do PetLife:
- **DB-05** RSA keys efêmeras deslogam todos os usuários a cada deploy
- **DB-01** Token refresh ausente — sessão expira em 15 minutos sem renovação
- **DB-03** DELETE /pets/{id} ausente — violação LGPD Art. 18, VI
- **DB-04** GET /veterinarians/favorites ausente — tela VetFavoritesPage completamente quebrada
- **DB-07** Rate limiting de login ausente — vulnerabilidade de força-bruta
- **DB-02** FCM token não registrado no frontend — push notifications nunca entregues

**Leia antes de implementar:**
- `docs/DOD.md` — critérios de qualidade
- `docs/ADR-004-jwt-rs256-security.md` — decisão de JWT
- `docs/ADR-005-lgpd-data-compliance.md` — LGPD
- `GEMINI.md` seção 12 — Regras de Ouro

---

## 1. FASE 1 — DB-05: Persistir RSA Keys (30 min)

### Contexto
Atualmente `RsaKeyConfig.java` gera um novo par de chaves RSA a cada inicialização da JVM.
Isso invalida todos os JWTs emitidos anteriormente, deslogando todos os usuários a cada deploy.

### 1.1 Backend

#### [MODIFY] `apps/backend/src/main/java/com/petlife/config/RsaKeyConfig.java`
```java
package com.petlife.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Lê chaves RSA de variáveis de ambiente (Base64-encoded).
 * NUNCA gerar chaves efemeramente — isso desloga todos os usuários a cada deploy.
 *
 * Geração do par de chaves:
 *   openssl genrsa -out private.pem 2048
 *   openssl pkcs8 -topk8 -inform PEM -in private.pem -out private_pkcs8.pem -nocrypt
 *   openssl rsa -in private.pem -pubout -out public.pem
 *   cat private_pkcs8.pem | base64 -w 0  -> PETLIFE_RSA_PRIVATE_KEY
 *   cat public.pem | base64 -w 0         -> PETLIFE_RSA_PUBLIC_KEY
 */
@Configuration
public class RsaKeyConfig {

    @Value("${petlife.security.rsa.private-key}")
    private String privateKeyBase64;

    @Value("${petlife.security.rsa.public-key}")
    private String publicKeyBase64;

    @Bean
    public RSAPrivateKey rsaPrivateKey() throws Exception {
        String cleaned = privateKeyBase64
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(cleaned);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    @Bean
    public RSAPublicKey rsaPublicKey() throws Exception {
        String cleaned = publicKeyBase64
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(cleaned);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
    }
}
```

#### [MODIFY] `apps/backend/src/main/resources/application.properties`
Adicionar (valores preenchidos via variáveis de ambiente no deploy):
```properties
petlife.security.rsa.private-key=${PETLIFE_RSA_PRIVATE_KEY}
petlife.security.rsa.public-key=${PETLIFE_RSA_PUBLIC_KEY}
```

#### [NEW] `.env.example` (na raiz do monorepo)
```bash
# RSA Keys — gerar com os comandos no RsaKeyConfig.java
PETLIFE_RSA_PRIVATE_KEY=<base64-encoded-pkcs8-private-key>
PETLIFE_RSA_PUBLIC_KEY=<base64-encoded-public-key>

# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/petlife
SPRING_DATASOURCE_USERNAME=petlife
SPRING_DATASOURCE_PASSWORD=petlife

# Redis
SPRING_DATA_REDIS_URL=redis://localhost:6379

# Firebase Admin SDK
GOOGLE_APPLICATION_CREDENTIALS=/path/to/firebase-service-account.json
```

### 1.2 Verificação FASE 1
```bash
cd apps/backend
mvn clean compile
# Deve compilar sem erros. Testar localmente com variáveis de ambiente configuradas.
```

---

## 2. FASE 2 — DB-01: Token Refresh (4h)

### Contexto
O PRD define JWT de 24 horas com refresh token de 30 dias (Seção 7.1.4).
Atualmente não existe `POST /auth/refresh`, forçando o usuário a re-logar a cada 15 minutos.

### 2.1 Backend

#### [MODIFY] `apps/backend/src/main/java/com/petlife/modules/auth/domain/port/TokenPort.java`
Adicionar métodos:
```java
// Adicionar aos métodos existentes:
String generateRefreshToken(User user);
UUID extractUserIdFromRefreshToken(String refreshToken);
boolean isRefreshTokenValid(String refreshToken);
```

#### [NEW] `apps/backend/src/main/java/com/petlife/modules/auth/application/usecase/RefreshTokenUseCase.java`
```java
package com.petlife.modules.auth.application.usecase;

import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.auth.domain.port.TokenPort;
import com.petlife.modules.auth.domain.port.UserRepositoryPort;
import com.petlife.modules.auth.infrastructure.dto.TokenPairResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

// SEM ApiResponse, SEM HttpStatus, SEM JpaRepository
@Component
@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private final TokenPort tokenPort;
    private final UserRepositoryPort userRepository;

    public TokenPairResponse execute(String refreshToken) {
        if (!tokenPort.isRefreshTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Refresh token inválido ou expirado");
        }

        UUID userId = tokenPort.extractUserIdFromRefreshToken(refreshToken);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("Usuário não encontrado"));

        String newAccessToken = tokenPort.generateAccessToken(user);
        String newRefreshToken = tokenPort.generateRefreshToken(user);

        return new TokenPairResponse(newAccessToken, newRefreshToken, user.getId().toString());
    }
}
```

#### [NEW] `apps/backend/src/main/java/com/petlife/modules/auth/infrastructure/dto/RefreshTokenRequest.java`
```java
package com.petlife.modules.auth.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
    @NotBlank(message = "Refresh token é obrigatório")
    String refreshToken
) {}
```

#### [NEW] `apps/backend/src/main/java/com/petlife/modules/auth/infrastructure/dto/TokenPairResponse.java`
```java
package com.petlife.modules.auth.infrastructure.dto;

public record TokenPairResponse(
    String accessToken,
    String refreshToken,
    String userId
) {}
```

#### [MODIFY] `apps/backend/src/main/java/com/petlife/modules/auth/infrastructure/controller/AuthController.java`
Adicionar endpoint:
```java
@PostMapping("/refresh")
public ResponseEntity<ApiResponse<TokenPairResponse>> refresh(
        @Valid @RequestBody RefreshTokenRequest request) {
    TokenPairResponse tokens = refreshTokenUseCase.execute(request.refreshToken());
    return ResponseEntity.ok(ApiResponse.success(tokens));
}
```
E injetar `RefreshTokenUseCase refreshTokenUseCase` no constructor.

### 2.2 Frontend

#### [MODIFY] `apps/web/src/infrastructure/http/api.ts`
Adicionar interceptor de resposta para renovação automática do token:
```typescript
// Adicionar após a criação do objeto `api`:
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        const refreshToken = tokenStorage.getRefreshToken();
        if (!refreshToken) throw new Error('Sem refresh token');
        const { data } = await axios.post('/auth/refresh', { refreshToken });
        const newAccessToken = data.data.accessToken;
        tokenStorage.setAccessToken(newAccessToken);
        tokenStorage.setRefreshToken(data.data.refreshToken);
        originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;
        return api(originalRequest);
      } catch {
        tokenStorage.clear();
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);
```

#### [MODIFY] `apps/web/src/infrastructure/storage/tokenStorage.ts`
Garantir que o storage salva/lê `refresh_token`:
```typescript
export const tokenStorage = {
  getAccessToken: () => localStorage.getItem('petlife_access_token'),
  setAccessToken: (token: string) => localStorage.setItem('petlife_access_token', token),
  getRefreshToken: () => localStorage.getItem('petlife_refresh_token'),
  setRefreshToken: (token: string) => localStorage.setItem('petlife_refresh_token', token),
  clear: () => {
    localStorage.removeItem('petlife_access_token');
    localStorage.removeItem('petlife_refresh_token');
  },
};
```

#### [MODIFY] `apps/web/src/infrastructure/http/auth.api.ts`
Salvar refresh_token no login:
```typescript
// No método de login, após receber resposta:
tokenStorage.setAccessToken(data.data.accessToken);
tokenStorage.setRefreshToken(data.data.refreshToken); // ADICIONAR
```

---

## 3. FASE 3 — DB-03: DELETE /pets/{id} + Cascade LGPD (6h)

### Contexto
Violação ativa do Art. 18, VI da LGPD. O usuário não consegue exercer o direito à eliminação
de dados do seu pet. Ver `docs/ADR-005-lgpd-data-compliance.md`.

### 3.1 Migration Flyway — PRIMEIRO

#### [NEW] `apps/backend/src/main/resources/db/migration/V{N}__add_cascade_delete_pet_children.sql`
```sql
-- LGPD Art. 18 VI — Garantir eliminação em cascata de todos os dados vinculados ao pet
-- Verificar constraints existentes antes de recriar

DO $$
BEGIN
    -- vaccinations
    IF EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'vaccinations_pet_id_fkey'
    ) THEN
        ALTER TABLE vaccinations DROP CONSTRAINT vaccinations_pet_id_fkey;
    END IF;
    ALTER TABLE vaccinations
        ADD CONSTRAINT vaccinations_pet_id_fkey
        FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE;

    -- consultations
    IF EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'consultations_pet_id_fkey'
    ) THEN
        ALTER TABLE consultations DROP CONSTRAINT consultations_pet_id_fkey;
    END IF;
    ALTER TABLE consultations
        ADD CONSTRAINT consultations_pet_id_fkey
        FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE;

    -- medications
    IF EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'medications_pet_id_fkey'
    ) THEN
        ALTER TABLE medications DROP CONSTRAINT medications_pet_id_fkey;
    END IF;
    ALTER TABLE medications
        ADD CONSTRAINT medications_pet_id_fkey
        FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE;

    -- groomings
    IF EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'groomings_pet_id_fkey'
    ) THEN
        ALTER TABLE groomings DROP CONSTRAINT groomings_pet_id_fkey;
    END IF;
    ALTER TABLE groomings
        ADD CONSTRAINT groomings_pet_id_fkey
        FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE;

    -- weight_logs (se existir tabela separada)
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'weight_logs') THEN
        IF EXISTS (
            SELECT 1 FROM information_schema.table_constraints
            WHERE constraint_name = 'weight_logs_pet_id_fkey'
        ) THEN
            ALTER TABLE weight_logs DROP CONSTRAINT weight_logs_pet_id_fkey;
        END IF;
        ALTER TABLE weight_logs
            ADD CONSTRAINT weight_logs_pet_id_fkey
            FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE;
    END IF;

    -- photos (se existir tabela separada)
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'photos') THEN
        IF EXISTS (
            SELECT 1 FROM information_schema.table_constraints
            WHERE constraint_name = 'photos_pet_id_fkey'
        ) THEN
            ALTER TABLE photos DROP CONSTRAINT photos_pet_id_fkey;
        END IF;
        ALTER TABLE photos
            ADD CONSTRAINT photos_pet_id_fkey
            FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE;
    END IF;

    -- routine_activities (se existir)
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'routine_activities') THEN
        IF EXISTS (
            SELECT 1 FROM information_schema.table_constraints
            WHERE constraint_name = 'routine_activities_pet_id_fkey'
        ) THEN
            ALTER TABLE routine_activities DROP CONSTRAINT routine_activities_pet_id_fkey;
        END IF;
        ALTER TABLE routine_activities
            ADD CONSTRAINT routine_activities_pet_id_fkey
            FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE;
    END IF;
END $$;
```

### 3.2 Backend

#### [MODIFY] `apps/backend/src/main/java/com/petlife/modules/pet/domain/port/PetRepositoryPort.java`
Adicionar:
```java
void deleteById(UUID petId);
```

#### [MODIFY] `apps/backend/src/main/java/com/petlife/modules/pet/infrastructure/persistence/adapter/PetPersistenceAdapter.java`
Implementar:
```java
@Override
public void deleteById(UUID petId) {
    jpaRepository.deleteById(petId);
}
```

#### [NEW] `apps/backend/src/main/java/com/petlife/modules/pet/application/usecase/DeletePetUseCase.java`
```java
package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.modules.pet.domain.port.PetRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

// SEM ApiResponse, SEM HttpStatus, SEM JpaRepository
@Component
@RequiredArgsConstructor
public class DeletePetUseCase {

    private final PetRepositoryPort petRepository;

    public void execute(UUID petId, UUID requestingUserId) {
        Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new IllegalArgumentException("Pet não encontrado: " + petId));

        if (!pet.getUserId().equals(requestingUserId)) {
            throw new SecurityException("Usuário não tem permissão para excluir este pet");
        }

        // Cascade delete via FK ON DELETE CASCADE no banco (Migration V{N})
        petRepository.deleteById(petId);
    }
}
```

#### [MODIFY] `apps/backend/src/main/java/com/petlife/modules/pet/infrastructure/controller/PetController.java`
Adicionar:
```java
@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void delete(
        @PathVariable UUID id,
        @AuthenticationPrincipal Jwt jwt) {
    UUID userId = UUID.fromString(jwt.getSubject());
    deletePetUseCase.execute(id, userId);
}
```
Injetar `DeletePetUseCase deletePetUseCase` no constructor.

### 3.3 Frontend

#### [MODIFY] `apps/web/src/infrastructure/http/pet.api.ts`
```typescript
// Adicionar ao objeto petApi:
delete: async (petId: string): Promise<void> => {
  await api.delete(`/pets/${petId}`);
},
```

#### [MODIFY] `apps/web/src/application/pet/usePets.ts` (ou hook equivalente)
```typescript
// Adicionar:
const deletePet = useCallback(async (petId: string) => {
  await petApi.delete(petId);
  // Remover da lista local
  setPets(prev => prev.filter(p => p.id !== petId));
}, []);

// Retornar no hook:
return { pets, loading, error, fetchPets, deletePet };
```

---

## 4. FASE 4 — DB-04: GET /veterinarians/favorites (4h)

### 4.1 Backend

#### [MODIFY] `apps/backend/src/main/java/com/petlife/modules/veterinarian/domain/port/VetFavoriteRepositoryPort.java`
Verificar se existe; adicionar se necessário:
```java
package com.petlife.modules.veterinarian.domain.port;

import com.petlife.modules.veterinarian.domain.entity.Veterinarian;
import com.petlife.shared.PagedResult;
import java.util.UUID;

public interface VetFavoriteRepositoryPort {
    void addFavorite(UUID userId, UUID vetId);
    void removeFavorite(UUID userId, UUID vetId);
    boolean isFavorite(UUID userId, UUID vetId);
    PagedResult<Veterinarian> findFavoritesByUserId(UUID userId, int page, int size); // NOVO
}
```

#### [NEW] `apps/backend/src/main/java/com/petlife/modules/veterinarian/application/usecase/GetVetFavoritesUseCase.java`
```java
package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.veterinarian.domain.entity.Veterinarian;
import com.petlife.modules.veterinarian.domain.port.VetFavoriteRepositoryPort;
import com.petlife.shared.PagedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetVetFavoritesUseCase {

    private final VetFavoriteRepositoryPort favoriteRepository;

    public PagedResult<Veterinarian> execute(UUID userId, int page, int size) {
        return favoriteRepository.findFavoritesByUserId(userId, page, size);
    }
}
```

#### [MODIFY] `apps/backend/src/main/java/com/petlife/modules/veterinarian/infrastructure/controller/VeterinarianController.java`
Adicionar:
```java
@GetMapping("/favorites")
public ResponseEntity<ApiResponse<PagedResult<VeterinarianResponse>>> listFavorites(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
    UUID userId = UUID.fromString(jwt.getSubject());
    PagedResult<Veterinarian> result = getVetFavoritesUseCase.execute(userId, page, size);
    PagedResult<VeterinarianResponse> response = new PagedResult<>(
        result.content().stream().map(vetMapper::toResponse).toList(),
        result.totalElements(), result.totalPages(), result.pageNumber()
    );
    return ResponseEntity.ok(ApiResponse.paged(response));
}
```

#### [MODIFY] Adapter de Favoritos
Implementar `findFavoritesByUserId` com JOIN entre `vet_favorites` e `veterinarians`.

### 4.2 Frontend

#### [MODIFY] `apps/web/src/infrastructure/http/veterinarian.api.ts`
```typescript
// Adicionar:
getFavorites: async (page = 0, size = 20): Promise<PagedResponse<VeterinarianResponse>> => {
  const { data } = await api.get('/veterinarians/favorites', { params: { page, size } });
  return data.data;
},
```

#### [MODIFY] `apps/web/src/application/veterinarian/useVetFavorites.ts`
Substituir mock/estado vazio por chamada real:
```typescript
const fetchFavorites = useCallback(async () => {
  setLoading(true);
  try {
    const result = await veterinarianApi.getFavorites();
    setFavorites(result.content);
  } catch {
    setError('Erro ao carregar favoritos');
  } finally {
    setLoading(false);
  }
}, []);
```

---

## 5. FASE 5 — DB-07: Rate Limiting de Login (4h)

### 5.1 Backend

#### [MODIFY] `apps/backend/pom.xml`
Adicionar dependency:
```xml
<!-- Rate Limiting -->
<dependency>
    <groupId>com.giffing.bucket4j.spring.boot.starter</groupId>
    <artifactId>bucket4j-spring-boot-starter</artifactId>
    <version>0.12.7</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

#### [NEW] `apps/backend/src/main/java/com/petlife/config/RateLimitingConfig.java`
```java
package com.petlife.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiting para tentativas de login.
 * Limite: 5 tentativas por IP a cada 5 minutos.
 * Produção: substituir ConcurrentHashMap por Redis via Bucket4j Spring Boot Starter.
 */
@Component
public class RateLimitingConfig {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String ip) {
        return buckets.computeIfAbsent(ip, this::newBucket);
    }

    private Bucket newBucket(String ip) {
        Bandwidth limit = Bandwidth.classic(
            5,
            Refill.intervally(5, Duration.ofMinutes(5))
        );
        return Bucket.builder().addLimit(limit).build();
    }

    public boolean tryConsume(String ip) {
        return resolveBucket(ip).tryConsume(1);
    }
}
```

#### [MODIFY] `apps/backend/src/main/java/com/petlife/modules/auth/infrastructure/controller/AuthController.java`
No endpoint de login, adicionar verificação antes da autenticação:
```java
@PostMapping("/login")
public ResponseEntity<ApiResponse<TokenPairResponse>> login(
        @Valid @RequestBody LoginRequest request,
        HttpServletRequest httpRequest) {
    String clientIp = httpRequest.getRemoteAddr();
    if (!rateLimitingConfig.tryConsume(clientIp)) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
            .body(ApiResponse.error("Muitas tentativas de login. Aguarde 5 minutos."));
    }
    // ... lógica existente de autenticação
}
```
Injetar `RateLimitingConfig rateLimitingConfig`.

---

## 6. FASE 6 — DB-02: FCM Token no Frontend (2h)

### 6.1 Frontend

#### [MODIFY] `apps/web/src/infrastructure/http/notification.api.ts`
Adicionar:
```typescript
registerDeviceToken: async (fcmToken: string): Promise<void> => {
  await api.post('/users/me/device-tokens', { token: fcmToken, platform: 'web' });
},

revokeDeviceToken: async (fcmToken: string): Promise<void> => {
  await api.delete('/users/me/device-tokens', { data: { token: fcmToken } });
},
```

#### [MODIFY] `apps/web/src/application/notification/usePushNotifications.ts`
Após obter o FCM token, registrar na API:
```typescript
// Após: const token = await getToken(messaging, { vapidKey: VAPID_KEY });
if (token) {
  // Armazenar localmente para revogação no logout
  localStorage.setItem('petlife_fcm_token', token);
  // REGISTRAR NA API — sem isso push nunca é entregue (DB-02)
  try {
    await notificationApi.registerDeviceToken(token);
  } catch (err) {
    console.error('[FCM] Falha ao registrar device token:', err);
    // Não bloquear o fluxo — tentar novamente na próxima sessão
  }
}
```

#### [MODIFY] Lógica de logout
Revogar token FCM ao deslogar:
```typescript
// Em useAuth.ts ou similar, no método logout:
const fcmToken = localStorage.getItem('petlife_fcm_token');
if (fcmToken) {
  try {
    await notificationApi.revokeDeviceToken(fcmToken);
  } finally {
    localStorage.removeItem('petlife_fcm_token');
  }
}
```

---

## 7. Regras Preventivas (SDD-001)

| # | Regra | Erro que previne |
|---|---|---|
| P1 | **RSA keys NUNCA geradas na JVM.** Ler sempre de variável de ambiente. | Usuários deslogados a cada deploy |
| P2 | **Refresh token NUNCA null no storage.** Verificar antes de chamar `/auth/refresh`. | NullPointerException no interceptor |
| P3 | **Cascade delete ANTES do UseCase.** Rodar a migration antes de implementar `DeletePetUseCase`. | FK violation ao tentar deletar pet com filhos |
| P4 | **Rate limiting NUNCA em domínio.** Pertence ao Controller (infraestrutura HTTP). | Violação hexagonal |
| P5 | **FCM token registrado APÓS login bem-sucedido.** Não bloquear fluxo se falhar. | UX travada por falha de notificação |
| P6 | **Nenhuma migration sem rollback verificado.** Testar `ALTER TABLE` em banco local antes de prod. | FK violation irreversível |

---

## 8. Gate Check — Executar após CADA fase

```bash
cd apps/backend

# 1. Compilação
mvn clean compile

# 2. ApiResponse em Use Cases (DEVE retornar vazio)
grep -rn "ApiResponse" src/main/java/com/petlife/modules/*/application/

# 3. JpaRepository/JpaEntity em application (DEVE retornar vazio)
grep -rn "JpaRepository\|JpaEntity" src/main/java/com/petlife/modules/*/application/

# 4. Arquivos sem package (DEVE retornar vazio)
grep -rL "^package " src/main/java/ --include="*.java"

# 5. Testes + cobertura (gate 85%)
mvn test

cd ../web

# 6. Frontend
pnpm lint && pnpm typecheck && pnpm test
```

## 9. Atualizar após concluir

- [x] `docs/compliance-report.md` — marcar DB-01 a DB-05, DB-07 como `✅`
- [x] `GEMINI.md` seção 8 — atualizar % de cobertura do M01 e M02
- [x] Criar PR: `feat(m01,m02): fecha gaps críticos DB-01 DB-02 DB-03 DB-04 DB-05 DB-07`

---

*SDD-001 criado pela Antigravity AI — 27/07/2026*
