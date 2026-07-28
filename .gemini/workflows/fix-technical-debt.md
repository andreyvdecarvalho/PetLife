# Workflow: Corrigir Débito Técnico

## Quando Usar
Use ao fechar qualquer débito técnico (DT-XX) listado no `GEMINI.md` seção 6.4,
ou ao corrigir violações arquiteturais identificadas.

## Mapa de Débitos Técnicos Prioritários

### DT-03 — RSA Keys Efêmeras (CRÍTICO)
**Problema:** Chaves RSA geradas efemeramente — todos os usuários são deslogados a cada deploy.
**Localização:** `apps/backend/src/main/java/com/petlife/config/RsaKeyConfig.java`
**Solução:** Persistir chaves em variável de ambiente ou secret manager.

```java
// Antes (problema)
// Gera par de chaves novo a cada inicialização

// Depois (correto)
@Configuration
public class RsaKeyConfig {
    @Value("${petlife.security.rsa.private-key}")
    private String privateKeyBase64;
    
    @Value("${petlife.security.rsa.public-key}")
    private String publicKeyBase64;
    
    @Bean
    public RSAPrivateKey privateKey() throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyBase64);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
    }
    
    @Bean
    public RSAPublicKey publicKey() throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
    }
}
```

```bash
# Gerar par de chaves RSA para configurar em .env
openssl genrsa -out private.pem 2048
openssl pkcs8 -topk8 -inform PEM -in private.pem -out private_pkcs8.pem -nocrypt
openssl rsa -in private.pem -pubout -out public.pem

# Base64 para .env
cat private_pkcs8.pem | base64 -w 0
cat public.pem | base64 -w 0
```

### DT-04 — PetSpecies Enum Duplicado (Frontend)
**Problema:** Enum `PetSpecies` definido em múltiplos lugares incompatíveis.
**Solução:** Centralizar em `src/domain/shared/Species.ts`

```ts
// apps/web/src/domain/shared/Species.ts (novo arquivo)
export type PetSpecies = 
  | 'dog' 
  | 'cat' 
  | 'bird' 
  | 'fish' 
  | 'rodent' 
  | 'reptile' 
  | 'other';

export const SPECIES_LABELS: Record<PetSpecies, string> = {
  dog: 'Cachorro',
  cat: 'Gato',
  bird: 'Pássaro',
  fish: 'Peixe',
  rodent: 'Roedor',
  reptile: 'Réptil',
  other: 'Outro',
};
```

### DT-05 — Estilos Inline em ProfilePage (Frontend)
**Problema:** `ProfilePage.tsx` tem estilos inline hardcoded (violação DOD).
**Solução:** Mover para `ProfilePage/styles.css` com tokens.

```bash
# Verificar estilos inline
grep -n "style={{" apps/web/src/pages/ProfilePage.tsx

# Mover para CSS com tokens
# Criar components/pages/ProfilePage/styles.css
```

### DT-06 — VetFavoritesPage com lógica inline (Frontend)
**Problema:** Lógica de UI complexa e CSS inline diretamente na página.
**Solução:** Extrair para organisms.

```
Criar: components/organisms/VetFavoritesList/
  - index.tsx
  - styles.css

VetFavoritesPage.tsx → importar VetFavoritesList
```

### DT-07 — WeightRecordResponse na camada errada (Frontend)
**Problema:** `WeightRecordResponse` em `infrastructure/dto` mas consumido em `application`.
**Solução:** Mover o tipo para `domain/pet/WeightRecord.ts`

```ts
// apps/web/src/domain/pet/WeightRecord.ts (novo)
export interface WeightRecord {
  id: string;
  petId: string;
  weightKg: number;
  measuredAt: string;
  source?: string;
  createdAt: string;
}
```

### DT-09 — DOD Desatualizado
**Problema:** DOD menciona "Service" mas arquitetura é hexagonal (Use Cases + Adapters).
**Solução:** Atualizar `docs/DOD.md` seção 1.1.

### DT-10 — Path Inconsistente de Auth Google
**Problema:** PRD define `/auth/oauth/google`, implementado como `/auth/google`.
**Solução:** Opções:
1. Renomear endpoint no backend para `/auth/oauth/google`
2. Ou documentar como ADR a diferença intencional

## Processo Geral

```bash
# 1. Criar branch
git checkout -b fix/dt-{ID}-{descricao}

# 2. Implementar correção
# Ver template específico do débito acima

# 3. Gate checks
cd apps/backend && mvn clean compile
cd apps/web && pnpm lint && pnpm typecheck

# 4. Testes
cd apps/backend && mvn test
cd apps/web && pnpm test

# 5. Atualizar GEMINI.md — marcar débito como fechado
# Seção 6.4 — riscar ou marcar como resolvido

# 6. Commit
git commit -m "fix: resolve débito técnico DT-{ID} — {descrição}"
```

## Referências
- `GEMINI.md` seção 6.4 — mapa de débitos técnicos
- `docs/SDD-hexagonal-refactoring.md` — regras preventivas
- `docs/DOD.md` — critérios de qualidade
- `docs/adr/` — decisões arquiteturais
