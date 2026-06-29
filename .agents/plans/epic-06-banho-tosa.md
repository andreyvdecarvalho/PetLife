# 📄 SDD - Épico 06: Banho e Tosa

## 1. Visão Geral
Gerenciamento das rotinas higiênicas e estéticas, essencial para raças que demandam cuidado contínuo. Prevê ciclo recorrente e comparação visual do serviço prestado (fotos antes/depois).

## 2. User Stories Cobertas
- **US-025**: Como tutor, quero registrar banho e tosa com data e pet shop
- **US-026**: Como tutor, quero definir periodicidade e receber lembretes
- **US-027**: Como tutor, quero anexar fotos antes/depois do procedimento

## 3. Modelo de Dados
Entidade `Grooming`:
- `id`: UUID v4
- `pet_id`: UUID v4
- `type`: Enum (bath, grooming, bath_and_grooming)
- `date`: Date
- `provider`: String(200) (Nome do Pet Shop)
- `cost`: Decimal(10,2)
- `frequency_days`: Integer (Opcional)
- `next_date`: Date (Calculado)
- `notes`: Text
- `photos`: JSON Array (Limitado a 2 URLs - "antes" e "depois")
- `created_at`: Timestamp

## 4. Regras de Negócio e Validações
- **Cálculo de Próxima Data**: `next_date = date + frequency_days`. Apenas gerada se `frequency_days` for informado.
- **Notificação**: 2 dias antes e no dia agendado.
- **Anexos Visuais**: Suporte a envio de imagens no upload (máximo 2 por registro), categorizadas internamente para renderizar side-by-side no app.

## 5. Endpoints da API (REST)
- `POST /api/v1/pets/{petId}/groomings` -> Registrar.
- `GET /api/v1/pets/{petId}/groomings` -> Listar (histórico).
- `PUT /api/v1/pets/{petId}/groomings/{id}` -> Editar procedimento existente.
- `POST /api/v1/pets/{petId}/groomings/{id}/photos` -> Envio de imagens (multipart/form-data com o tag `before` ou `after`).

## 6. Componentes de UI (Frontend/Mobile)
- **GroomingForm**: Seletor de tipo de serviço (Toggle/Radio buttons). Input numérico para "Refazer a cada X dias".
- **BeforeAfterViewer**: Componente de visualização com barra deslizante para comparar o pet antes e depois da tosa.

## 7. Critérios de Aceite
- [ ] Gravação do serviço (Banho, Tosa ou Ambos) com a data obrigatória.
- [ ] Geração do `next_date` automático se preenchida a frequência.
- [ ] Componente visual suportando 2 imagens interativas.
