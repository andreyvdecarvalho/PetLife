# ADR-006: Abordagem de Inicialização do Flyway via BeanPostProcessor

## Status
Aceito

## Contexto
O PetLife utiliza o Spring Boot 4.1.0 e Docker Compose para subir a aplicação. Durante o processo de build multi-stage para a imagem Docker, utilizamos a ferramenta de extração de layers (`jarmode=tools extract`). Ao iniciar a aplicação com a configuração `spring.jpa.hibernate.ddl-auto=validate`, notamos que o Hibernate falhava silenciosamente antes da execução do Flyway, uma vez que as tabelas necessárias para a validação do schema não existiam (e o Flyway ainda não tinha sido disparado devido a ausência de auto-configuração nativa do Flyway compatível sendo executada antes do Hibernate).

Para o correto funcionamento do ambiente, era fundamental garantir que:
1. O banco de dados passasse pelas "migrations" via Flyway ANTES que o Hibernate iniciasse a validação das tabelas.
2. Não fossem inseridos scripts manuais ou hacks externos fora do ecossistema do Spring.

## Decisão
Criou-se a classe `FlywayConfig` atuando como um `BeanPostProcessor`. Ela intercepta a criação dos beans do tipo `DataSource` e executa a migração (`Flyway.configure()...migrate()`) imediatamente após o `DataSource` estar pronto, mas **antes** do `EntityManagerFactory` do Hibernate ser instanciado, visto que ele depende do `DataSource`.

## Consequências
- **Positivas**: 
  - A migração e validação de schema ocorrem na ordem estrita necessária (primeiro as tabelas são criadas/atualizadas pelo Flyway e, em seguida, validadas pelo Hibernate).
  - O container do backend não quebra ao iniciar do zero, sendo resiliente mesmo com banco de dados limpo.
- **Negativas**:
  - Essa configuração requer a injeção manual das configurações via código ou environment (`SPRING_FLYWAY_URL`, `SPRING_FLYWAY_USER`), ao invés de depender apenas de propriedades de auto-configuration.
