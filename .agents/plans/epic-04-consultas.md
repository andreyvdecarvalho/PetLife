# 📄 SDD - Épico 04: Consultas Veterinárias

## 1. Visão Geral
Módulo que permite o acompanhamento clínico. O tutor registrará as visitas ao veterinário, salvando prescrições, resultados de exames e agendando retornos.

## 2. User Stories Cobertas
- **US-016**: Como tutor, quero registrar consultas com data, motivo e diagnóstico
- **US-017**: Como tutor, quero anexar exames e receitas às consultas
- **US-018**: Como tutor, quero agendar retornos e receber lembretes
- **US-019**: Como tutor, quero que o peso registrado na consulta atualize o perfil do pet

## 3. Modelo de Dados
Entidade `Consultation`:
- `id`: UUID v4
- `pet_id`: UUID v4
- `date`: DateTime
- `veterinarian`, `clinic`: String(200)
- `reason`: String(500) (Obrigatório)
- `diagnosis`, `prescriptions`, `notes`: Text
- `weight_at_visit`: Decimal(5,2)
- `follow_up_date`: Date
- `cost`: Decimal(10,2)
- `attachments`: JSON Array (URLs de arquivos no S3)
- `created_at`: Timestamp

## 4. Regras de Negócio e Validações
- **Peso do Pet**: Se `weight_at_visit` for fornecido e for a consulta mais recente, o sistema deve atualizar automaticamente o `weight_kg` do registro do `Pet` e inserir um log no histórico de pesos.
- **Agendamento de Retorno**: Se `follow_up_date` for preenchido, gerar um evento de lembrete (Push: 3 dias antes + dia D).
- **Anexos Múltiplos**: Limite de 5 anexos por consulta, até 2MB cada um. Tipos aceitos: JPEG, PNG, PDF.

## 5. Endpoints da API (REST)
- `POST /api/v1/pets/{petId}/consultations` -> Criar consulta.
- `GET /api/v1/pets/{petId}/consultations` -> Listar consultas.
- `POST /api/v1/pets/{petId}/consultations/{id}/attachments` -> Enviar anexos múltiplos.
- `DELETE /api/v1/pets/{petId}/consultations/{id}/attachments/{index}` -> Remover um anexo.

## 6. Componentes de UI (Frontend/Mobile)
- **ConsultationForm**: Formulário expansível. Diagnósticos e prescrições podem ser campos de texto longo.
- **AttachmentManager**: Componente drag-n-drop (Web) e seletor de arquivos (Mobile) para múltiplos envios com preview de thumbnails para imagens.
- **Timeline Integration**: Exibição simplificada desta consulta na linha do tempo principal.

## 7. Critérios de Aceite
- [x] Salvamento da consulta informando ao menos data e motivo.
- [x] Suporte até 5 arquivos/documentos (PDF/Imagens).
- [x] Atualização automática do peso do pet.
- [x] Criação de alerta agendado de retorno se data informada.

> **NOTA DE ARQUITETURA FRONTEND:** Para as telas desta Epic, siga rigorosamente o Atomic Design (Atoms, Molecules, Organisms, Templates, Pages). Cada componente em pasta própria com index.tsx e styles.css. Use Vanilla CSS (BEM / namespace semântico) consumindo variáveis do 	heme.css. Atoms não devem possuir margens e devem ocupar 100% (width: 100%), delegando posicionamento aos Organisms/Templates.
