# ADR 004: Estrutura de Segurança de Autenticação com JWT RS256 e BCrypt

## Status
Aprovado

## Data
30 de Junho de 2026

## Contexto
O PetLife manipula dados sensíveis de usuários (tutores de animais de estimação) e prestadores de serviços. A autenticação e a autorização de rotas precisam de um mecanismo seguro, escalável, stateless (sem sessões persistidas no backend para melhor escalabilidade) e resistente a ataques comuns de força bruta ou interceptação.

O uso de algoritmos de assinatura simétrica (como HMAC-SHA256) exige o compartilhamento de um segredo único entre todas as partes. Caso esse segredo seja comprometido em uma aplicação parceira ou no client, todo o ecossistema de autenticação é comprometido.

## Decisão
Decidimos implementar a segurança de autenticação no PetLife utilizando o **Spring Security** com os seguintes componentes:

1. **Assinatura Assimétrica RS256**:
   A autenticação e assinatura de tokens JWT utilizará criptografia de chave assimétrica **RS256** (RSA Signature com SHA-256). O backend detém a chave privada de forma secreta e segura para assinar os tokens, e expõe a chave pública para que qualquer cliente ou serviço possa validar a autenticidade do token de forma independente sem conhecer o segredo de assinatura.
2. **Ciclo de Vida de Tokens Estrito**:
   - **Access Token**: JWT contendo informações do usuário (`userId`, `roles`, `plan`, `email`) válido por **15 minutos** para diminuir a janela de uso em caso de roubo do token.
   - **Refresh Token**: Token aleatório seguro armazenado em banco de dados ou cookie seguro HTTP-only, válido por **30 dias**, utilizado para renovar o Access Token sem forçar o usuário a refazer o login.
3. **Criptografia de Senhas (BCrypt)**:
   As senhas cadastradas serão transformadas em hash criptográfico obrigatoriamente usando `BCryptPasswordEncoder` com **força (strength) 12**, garantindo alta resistência a ataques de força bruta e dicionário.
4. **Proteção de Endpoint e Rate Limiting**:
   Todas as rotas sob `/api/v1/` exigem token JWT válido, com exceção de endpoints públicos explicitamente configurados (como cadastro, login, swagger-ui e saúde do sistema). O sistema de autenticação bloqueia logins falhos repetidos (5 tentativas incorretas em 5 minutos) usando rate limiting.

## Consequências
### Positivas:
- Segurança robusta contra vazamento de chaves simétricas por meio do uso de RS256.
- Curto tempo de vida do Access Token minimiza o impacto caso o token seja interceptado.
- Criptografia de senhas extremamente segura com BCrypt (strength 12).
- Autenticação descentralizada de microsserviços facilitada pela chave pública.

### Negativas / Custos:
- Maior consumo de processamento (CPU) no backend devido a cálculos matemáticos complexos envolvidos nas chaves assimétricas RSA do JWT e no hashing BCrypt 12 (embora aceitável para os padrões atuais de hardware).
- Complexidade adicional no gerenciamento de chaves públicas/privadas.
