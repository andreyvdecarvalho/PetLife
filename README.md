# PetLife

**Toda a vida do seu pet em um só lugar**

Plataforma unificada para gestão da vida e saúde de animais de estimação, conectando tutores, veterinários e prestadores de serviços de forma simples e intuitiva.

## 🛠️ Stack Tecnológica

* **Backend**: Java 21 + Spring Boot (Maven), Clean Architecture e TDD.
* **Frontend Web**: React 19 + Vite (PWA) e TypeScript, estruturado com Atomic Design e Vanilla CSS.
* **Mobile**: React Native.
* **Infraestrutura**: PostgreSQL (Flyway), Redis e RabbitMQ.

## 📦 Principais Funcionalidades

* **Perfil e Saúde**: Registro completo do pet com foto, espécie, histórico clínico e diário de vacinas.
* **Rotina e Planejamento**: Calendário interativo para controle de alimentação, banhos, passeios e outras atividades.
* **Controle de Medicamentos**: Gerenciamento de tratamentos, dosagens e lembretes de administração de remédios.
* **Agendamento de Consultas**: Marcação de visitas veterinárias, controle de histórico e catálogo de profissionais (CRMV verificado).
* **Diário de Memórias**: Armazenamento de momentos e fotos inesquecíveis do pet.

## 🚀 Como Executar

### Pré-requisitos
* Java 21+ e Maven
* Node.js 22+ e pnpm
* Docker e Docker Compose (para banco de dados e mensageria)

### Passos

1. **Subir Infraestrutura**
   Na raiz do projeto, inicie os contêineres:
   ```bash
   docker-compose up -d
   ```

2. **Rodar o Backend**
   Em um terminal, acesse a pasta `apps/backend` e inicie a API:
   ```bash
   mvn clean install -DskipTests
   mvn spring-boot:run
   ```

3. **Rodar o Frontend Web**
   Em outro terminal, acesse a pasta `apps/web`, instale as dependências e inicie o dev server:
   ```bash
   pnpm install
   pnpm run dev
   ```

4. **Acesso**
   Abra o navegador em `http://localhost:5173`.
