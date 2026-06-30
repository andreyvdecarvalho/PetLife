# 📄 SDD - Épico 07: Linha do Tempo e Exportação

## 1. Visão Geral
Consolida todas as informações clínicas e corriqueiras do pet em uma visualização unificada (Timeline). Fornece também a geração de um passaporte clínico em PDF, ideal para viagens ou mudança de veterinário.

## 2. User Stories Cobertas
- **US-028**: Como tutor, quero ver todos os eventos do pet em uma linha do tempo
- **US-029**: Como tutor, quero filtrar a timeline por tipo de evento
- **US-030**: Como tutor, quero exportar o histórico completo do pet em PDF

## 3. Modelo de Dados / Arquitetura
A Linha do tempo não possui uma tabela única, mas sim um serviço de agregação (`EventsService`) que consolida dados das tabelas: `Vaccinations`, `Consultations`, `Medications`, `Groomings` e aniversários calculados via `birth_date`.

Estrutura da Resposta da API (DTO `TimelineEvent`):
- `id`: UUID (da origem)
- `type`: Enum (VACCINE, CONSULTATION, MEDICATION_START, MEDICATION_END, GROOMING, PHOTO, WEIGHT, BIRTHDAY)
- `date`: DateTime
- `title`, `description`: String
- `icon`: String / Emoji
- `color`: Hexcode
- `photo_url`: String (opcional)

## 4. Regras de Negócio e Validações
- **Ordenação**: Sempre decrescente (mais recentes no topo). Paginação via *cursor-based* ou *offset-limit* com páginas de tamanho 20.
- **Exportação (PDF)**: O `PDFService` no backend (ex: JasperReports ou HTML-to-PDF via Puppeteer/iText) deve consumir os dados do pet, consolidar a timeline (limitada a um range de datas opcional) e montar um documento limpo. Processamento pode demorar até 10s (assíncrono ou síncrono com loading). Só clientes Premium exportam PDFs (validar Subscription).

## 5. Endpoints da API (REST)
- `GET /api/v1/pets/{petId}/timeline?types={csv}&page=0&size=20` -> Listagem agregada.
- `GET /api/v1/pets/{petId}/export` -> Gera o PDF e retorna um `application/pdf` via Stream ou presigned URL do S3.

## 6. Componentes de UI (Frontend/Mobile)
- **TimelineView**: Scroll infinito, renderiza os componentes `TimelineNode` conectados visualmente por uma linha lateral.
- **FilterChips**: Botões rápidos no topo para filtrar tipos de eventos ("Apenas Vacinas", "Saúde", "Estética").
- **PDFPreview / DownloadButton**: Inicia o carregamento e apresenta o arquivo nativamente no SO ao concluir.

## 7. Critérios de Aceite
- [ ] Timeline traz dados cruzados (Vacinas + Consultas + Banhos + etc) sem gaps.
- [ ] Filtragem exclui eventos indesejados dinamicamente.
- [ ] Geração do arquivo PDF inclui capa (nome, foto e info do pet) e log de eventos estruturado.

> **NOTA DE ARQUITETURA FRONTEND:** Para as telas desta Epic, siga rigorosamente o Atomic Design (Atoms, Molecules, Organisms, Templates, Pages). Cada componente em pasta própria com index.tsx e styles.css. Use Vanilla CSS (BEM / namespace semântico) consumindo variáveis do 	heme.css. Atoms não devem possuir margens e devem ocupar 100% (width: 100%), delegando posicionamento aos Organisms/Templates.
