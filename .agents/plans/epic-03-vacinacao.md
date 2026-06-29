# 📄 SDD - Épico 03: Vacinação

## 1. Visão Geral
Módulo destinado ao controle do calendário vacinal do pet. Inclui previsão inteligente de próximas doses e lembretes automáticos para que o tutor não perca as datas.

## 2. User Stories Cobertas
- **US-011**: Como tutor, quero registrar uma vacina com nome, data e veterinário
- **US-012**: Como tutor, quero que o sistema sugira vacinas padrão para meu tipo de pet
- **US-013**: Como tutor, quero receber lembretes antes do vencimento de uma vacina
- **US-014**: Como tutor, quero anexar foto do comprovante de vacinação
- **US-015**: Como tutor, quero ver o histórico completo de vacinas do meu pet

## 3. Modelo de Dados
Entidade `Vaccination`:
- `id`: UUID v4 (PK)
- `pet_id`: UUID v4 (FK)
- `vaccine_name`: String(200)
- `date_administered`: Date
- `next_dose_date`: Date (Deve ser >= date_administered)
- `veterinarian`, `clinic`: String(200)
- `batch_number`, `manufacturer`: String
- `proof_url`: String(500)
- `notes`: Text
- `reminder_active`: Boolean (Default: true)
- `created_at`: Timestamp

## 4. Regras de Negócio e Validações
- **Sugestão de Vacinas**: Para cães (V8/V10, Antirrábica, Gripe, Giardíase, Leishmaniose). Para gatos (V3/V4/V5, Antirrábica, FeLV). Todas com reforço anual por padrão.
- **Cálculo da Próxima Dose**: O frontend/backend deve sugerir a `next_dose_date` somando 1 ano caso seja vacina padrão, com opção do usuário alterar.
- **Lembretes**: Notificação push deve ser programada para envio 7 dias antes e no dia de `next_dose_date`.
- **Anexos**: Foto de comprovante limite de 2MB.

## 5. Endpoints da API (REST)
- `POST /api/v1/pets/{petId}/vaccines` -> Registrar vacina.
- `GET /api/v1/pets/{petId}/vaccines` -> Listar histórico (ordem cronológica reversa).
- `GET /api/v1/vaccines/suggestions?species={species}` -> Retorna catálogo padrão.
- `PUT /api/v1/pets/{petId}/vaccines/{id}` -> Editar (inclui inativar lembrete).
- `POST /api/v1/pets/{petId}/vaccines/{id}/proof` -> Upload de anexo.

## 6. Componentes de UI (Frontend/Mobile)
- **VaccineList**: Histórico em ordem cronológica reversa.
- **VaccineForm**: Autocomplete para nome de vacinas sugeridas baseado na espécie do Pet.
- **ProofUploader**: Upload do adesivo/carimbo da vacina.

## 7. Critérios de Aceite
- [ ] Cadastro com data e nome da vacina.
- [ ] Autocomplete inteligente exibindo catálogo de vacinas baseado na espécie.
- [ ] Sistema projeta a próxima data automaticamente para vacinas padrão.
- [ ] Exibe comprovante expandido ao clicar.
- [ ] Exibição mais recente primeiro.
