# PetLife

PetLife é uma plataforma completa e inteligente para o gerenciamento da vida, saúde e bem-estar do seu pet. Desenvolvida para tutores e profissionais, a aplicação centraliza rotinas, medicações e histórico clínico em um único lugar.

## Stack Tecnológica
- **Backend:** Java 21, Spring Boot 3, Spring Data JPA, PostgreSQL, Flyway, Redis, RabbitMQ. (Clean Architecture + TDD).
- **Frontend Web:** React 19, Vite, TypeScript, Vanilla CSS (Atomic Design, BEM).
- **Mobile:** React Native 0.76.0.

## Principais Funcionalidades
- **Gestão de Perfil e Saúde:** Cadastro de pets, controle de peso e armazenamento de registros clínicos.
- **Controle de Medicamentos:** Acompanhamento de tratamentos ativos, agendamento de horários e registro de aderência.
- **Minha Rotina:** Planejamento diário e recorrente (passeios, banhos, etc.) e calendário integrado.
- **Agendamentos:** Marcação de retorno veterinário e consultas.
- **Diário de Memórias:** Registro de fotos e momentos especiais do pet.

## Execução Rápida

1. **Subir Banco de Dados e Serviços:**
```bash
docker-compose up -d
```

2. **Iniciar Backend:**
```bash
cd apps/backend
mvn spring-boot:run
```

3. **Iniciar Frontend:**
```bash
cd apps/web
pnpm install
pnpm run dev
```

Acesse a aplicação web via `http://localhost:5173`.
