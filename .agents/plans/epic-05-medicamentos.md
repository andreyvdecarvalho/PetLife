# 📄 SDD - Épico 05: Medicamentos

## 1. Visão Geral
Gerenciamento de tratamentos crônicos ou agudos. Lida com automação de posologias e disparo de lembretes em horários exatos da administração do remédio.

## 2. User Stories Cobertas
- **US-020**: Como tutor, quero cadastrar medicamentos com dosagem e horários
- **US-021**: Como tutor, quero receber lembretes no horário de cada dose
- **US-022**: Como tutor, quero marcar doses como tomadas, puladas ou atrasadas
- **US-023**: Como tutor, quero ver a aderência ao tratamento em porcentagem
- **US-024**: Como tutor, quero encerrar um tratamento manualmente

## 3. Modelo de Dados
Entidade Pai `Medication`:
- `id`: UUID v4
- `pet_id`: UUID v4
- `name`, `dosage`: String
- `frequency`: Enum (once, daily, twice_daily, every_8h, every_12h, weekly, custom)
- `custom_frequency_hours`: Integer
- `start_date`, `end_date`: Date
- `times_of_day`: JSON Array (ex: ["08:00", "20:00"])
- `status`: Enum (active, completed, cancelled)

Entidade Filha `MedicationAdministration` (Log de Doses):
- `id`: UUID
- `medication_id`: UUID v4
- `scheduled_time`: DateTime
- `administered_at`: DateTime (nullable)
- `status`: Enum (pending, taken, skipped, late)
- `skipped_reason`: String

## 4. Regras de Negócio e Validações
- **Geração de Logs**: Ao criar ou atualizar o `Medication`, o backend deve projetar as instâncias de `MedicationAdministration` (doses pendentes) entre o momento atual e a `end_date` (ou por um limite móvel de ex: 30 dias se contínuo).
- **Lembretes Exatos**: Push enviado exatamente no horário previsto na dose pendente. Se após 30min continuar `pending`, enviar segundo aviso (Push).
- **Fuso Horário**: Os arrays `times_of_day` são armazenados baseados no UTC, ou com offset correspondente, o timezone do usuário deve ser levado em conta para a execução do scheduler (RabbitMQ / Cron).
- **Aderência**: Calculada via `(tomadas / total_esperado) * 100`.

## 5. Endpoints da API (REST)
- `POST /api/v1/pets/{petId}/medications` -> Cadastrar tratamento.
- `GET /api/v1/pets/{petId}/medications` -> Listar ativos e finalizados.
- `PATCH /api/v1/medications/doses/{doseId}` -> Atualizar dose (tomada/pulada).
- `PATCH /api/v1/medications/{id}/stop` -> Encerrar manualmente.
- `GET /api/v1/pets/{petId}/medications/adherence` -> Retorna as métricas de aderência.

## 6. Componentes de UI (Frontend/Mobile)
- **TimePicker Array**: Controle para adicionar N horários.
- **MedicationCard**: Com barra de progresso / aderência circular.
- **DoseActionButtons**: Botões rápidos de ação "Tomar" e "Pular" para a dose atual, tanto in-app quanto em notificações interativas (Push Notifications Actions).

## 7. Critérios de Aceite
- [x] Geração correta dos registros das doses futuras.
- [x] Lembrete emitido no exato instante configurado.
- [x] Indicador de aderência atualiza em tempo real ao interagir com uma dose.
- [x] Permitir encerramento abrupto do tratamento (muda status, limpa doses pendentes futuras).

> **NOTA DE ARQUITETURA FRONTEND:** Para as telas desta Epic, siga rigorosamente o Atomic Design (Atoms, Molecules, Organisms, Templates, Pages). Cada componente em pasta própria com index.tsx e styles.css. Use Vanilla CSS (BEM / namespace semântico) consumindo variáveis do 	heme.css. Atoms não devem possuir margens e devem ocupar 100% (width: 100%), delegando posicionamento aos Organisms/Templates.
