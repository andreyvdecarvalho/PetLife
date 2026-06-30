# ADR 003: Arquitetura do Backend com Spring Boot e Boas Práticas

## Status
Aprovado

## Data
30 de Junho de 2026

## Contexto
O ecossistema PetLife necessita de um backend robusto, escalável, seguro e de alta performance para processar a lógica de negócios, faturamento, notificações push, histórico médico de pets e integração de terceiros. A modelagem de dados e a arquitetura das APIs precisam de padrões bem definidos para manter a consistência, integridade dos dados e facilitar o trabalho em equipe.

## Decisão
Decidimos construir o backend utilizando **Java 21** e **Spring Boot 4.1.0** (gerenciado por Maven 3.9.9), integrando PostgreSQL 16.4 e migrações estruturadas via Flyway 10.20.0.

Adotamos os seguintes padrões arquiteturais:
1. **Estrutura de Pacotes Orientada a Módulos**:
   O código do backend é estruturado sob `com.petlife` da seguinte forma:
   - `/config`: Configurações globais do Spring Boot.
   - `/shared`: Exceções, segurança e responses comuns a todo o sistema.
   - `/modules/<nome_modulo>`: Contém a lógica encapsulada de cada domínio do negócio (ex: `/auth`, `/pet`, `/medication`), dividindo-se em:
     - `controller/`: Camada de entrada REST (exposição de endpoints).
     - `service/`: Camada onde reside **100% da lógica de negócio**. Controllers e Repositories nunca devem conter regras de negócio.
     - `repository/`: Camada de acesso a dados (Spring Data JPA).
     - `entity/`: Entidades persistidas mapeadas com Hibernate.
     - `dto/`: Objetos de transferência de dados com validações robustas do Bean Validation (`jakarta.validation`).
2. **Identificadores Únicos Universais (UUID)**:
   Todas as chaves primárias (`id`) das tabelas e entidades no banco de dados devem usar obrigatoriamente `UUID` v4 gerado de forma automática no backend ou banco de dados, mitigando riscos de segurança como enumeração de IDs expostos na URL.
3. **Auditoria Automatizada**:
   Entidades utilizam campos de auditoria `createdAt` e `updatedAt` mapeados de forma transparente via anotações `@CreationTimestamp` e `@UpdateTimestamp` e `@EnableJpaAuditing` na classe principal.
4. **Gerenciamento de Schema com Flyway**:
   Qualquer alteração no banco de dados deve ser introduzida via migrações SQL versionadas na pasta `src/main/resources/db/migration` seguindo a ordem de versão `V1__...`, `V2__...`. O Hibernate deve ser configurado com `spring.jpa.hibernate.ddl-auto=validate` em ambientes de desenvolvimento e produção, delegando a criação/modificação estrutural inteiramente ao Flyway.

## Consequências
### Positivas:
- Alta coesão e baixo acoplamento devido à organização modular.
- Isolamento total da lógica de negócio na camada Service, facilitando testes unitários isolados.
- Rastreabilidade de alterações no banco de dados com migrações Flyway bem definidas.
- Redução de vulnerabilidades de segurança (ID enumeration) por meio de chaves primárias em UUID.

### Negativas / Custos:
- Maior overhead na criação de DTOs, mappers e classes de serviço para operações simples (CRUD).
- Necessidade de gerenciar e debugar migrações SQL manualmente em caso de falha de syntax ou execução.
