# 📄 SDD - Épico 08: Notificações

## 1. Visão Geral
Módulo central de comunicação do aplicativo. Responsável pelo engajamento e utilidade principal do produto (lembrar o usuário de não esquecer dos cuidados dos pets).

## 2. User Stories Cobertas
- **US-031**: Como tutor, quero configurar quais tipos de notificação desejo receber
- **US-032**: Como tutor, quero ver uma central de notificações dentro do app
- **US-033**: Como tutor, quero executar ações rápidas a partir da notificação

## 3. Modelo de Dados
Entidade `NotificationMessage` (Central In-App):
- `id`: UUID v4
- `user_id`: UUID v4
- `type`: Enum (VACCINE_REMINDER, MEDICATION_REMINDER, CONSULTATION_REMINDER, GROOMING_REMINDER, SYSTEM)
- `title`, `body`: String
- `target_id`: UUID (ID do pet ou evento relacionado)
- `read`: Boolean (default: false)
- `created_at`: Timestamp

Entidade `NotificationPreferences`:
- `user_id`: UUID v4 (PK)
- `push_enabled`: Boolean
- `email_enabled`: Boolean
- Configurações granulares (JSON/Booleans): `vaccines`, `medications`, `appointments`, `grooming`, `marketing`.
- `do_not_disturb_start`, `do_not_disturb_end`: Time (ex: "22:00", "07:00").

## 4. Regras de Negócio e Validações
- **Mensageria Assíncrona**: O agendador (`Spring Scheduler`) verifica doses, vacinas, etc., e posta eventos no RabbitMQ. O serviço de notificação consome a fila e despacha para o Firebase Cloud Messaging (FCM).
- **Limites e Silêncio**: O sistema deve checar as `NotificationPreferences`. Se for horário de *Do Not Disturb*, segurar a notificação até as 07:00 local (exceto medicamentos marcados como urgentes - regra futura).
- **Notificação Interativa (Actions)**: Push de medicamento deve ter ação `[TOMADO]`. O clique interage com backend direto ou abre o app na tela correta via deep link.

## 5. Endpoints da API (REST)
- `GET /api/v1/notifications` -> Lista da central in-app (paginado).
- `PATCH /api/v1/notifications/{id}/read` -> Marca lida.
- `PATCH /api/v1/notifications/read-all` -> Marca todas como lidas.
- `GET /api/v1/users/me/notification-preferences` -> Retorna opções.
- `PUT /api/v1/users/me/notification-preferences` -> Atualiza configurações e horários.
- `POST /api/v1/users/me/device-tokens` -> Registra token do FCM para o dispositivo logado.

## 6. Componentes de UI (Frontend/Mobile)
- **NotificationCenter**: Tela com abas (Não Lidas / Todas). Swipe to delete / Swipe to read.
- **Settings/PreferencesScreen**: Toggles para ligar/desligar categorias. Input de tempo para DND (Do Not Disturb).
- **Push Handlers (React Native)**: Listeners no background (Notifee/Firebase) para capturar o toque nos botões da notificação e executar requisição.

## 7. Critérios de Aceite
- [ ] Usuário consegue silenciar categoria específica de notificação e backend obedece.
- [ ] Push é emitido, mesmo com app fechado, via FCM/APNs.
- [ ] Ação rápida do push chama deep link ou endpoint diretamente, atualizando a dose.
- [ ] Tela de central de notificações com contador (badge) limpo ao abrir.

> **NOTA DE ARQUITETURA FRONTEND:** Para as telas desta Epic, siga rigorosamente o Atomic Design (Atoms, Molecules, Organisms, Templates, Pages). Cada componente em pasta própria com index.tsx e styles.css. Use Vanilla CSS (BEM / namespace semântico) consumindo variáveis do 	heme.css. Atoms não devem possuir margens e devem ocupar 100% (width: 100%), delegando posicionamento aos Organisms/Templates.
