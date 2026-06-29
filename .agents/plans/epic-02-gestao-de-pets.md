# 📄 SDD - Épico 02: Gestão de Pets

## 1. Visão Geral
Este épico abrange a criação e gestão do perfil dos animais de estimação. Permite que o tutor (usuário) cadastre, visualize, edite e arquive as informações de seus pets, formando o núcleo do sistema.

## 2. User Stories Cobertas
- **US-006**: Como tutor, quero cadastrar um pet com nome, espécie, raça e foto
- **US-007**: Como tutor, quero cadastrar múltiplos pets na mesma conta
- **US-008**: Como tutor, quero editar as informações do meu pet
- **US-009**: Como tutor, quero arquivar o perfil de um pet sem perder dados
- **US-010**: Como tutor, quero ver o peso do meu pet ao longo do tempo em um gráfico

## 3. Modelo de Dados (JPA/Hibernate)
A entidade principal é `Pet`.
Campos:
- `id`: UUID v4 (PK)
- `user_id`: UUID v4 (FK -> Users)
- `name`: String(100), obrigatório.
- `species`: Enum (dog, cat, bird, fish, rodent, reptile, other).
- `breed`: String(100), opcional.
- `sex`: Enum (male, female, unknown).
- `birth_date`: Date, não pode ser data futura.
- `weight_kg`: Decimal(5,2).
- `size`: Enum (mini, small, medium, large, giant).
- `neutered`: Boolean (default: false).
- `microchip_id`: String(50).
- `allergies`, `notes`: Text.
- `photo_url`: String(500).
- `status`: Enum (active, archived, deceased).
- `created_at`, `updated_at`: Timestamp de auditoria.

*Soft-delete*: A exclusão do pet da visão principal pode ser feita alterando o status para `archived`. Mas a exclusão real da conta do usuário apagará os pets via `CascadeType.REMOVE`.

## 4. Regras de Negócio e Validações
- **Validação de Limites**: O plano Free permite apenas 2 pets. O backend deve validar se o usuário excedeu o limite antes de salvar. Premium permite pets ilimitados.
- **Validações de Campo**: `name` deve ter no mínimo 2 caracteres. Foto comprimida para no máximo 500KB antes de subir (S3).
- **Relacionamentos**: Todos os registros dependentes de pet (vacinas, banho, etc.) devem ser excluídos se o pet for permanentemente deletado (Cascade delete - requisito LGPD).

## 5. Endpoints da API (REST)
- `POST /api/v1/pets` -> Cadastrar novo pet (Valida limite plano Free).
- `GET /api/v1/pets` -> Listar pets do usuário logado (paginado).
- `GET /api/v1/pets/{id}` -> Detalhes do pet.
- `PUT /api/v1/pets/{id}` -> Editar dados.
- `PATCH /api/v1/pets/{id}/status` -> Arquivar/Desarquivar.
- `POST /api/v1/pets/{id}/photo` -> Upload de foto (multipart/form-data).
- `GET /api/v1/pets/{id}/weight-history` -> Histórico de pesos (para o gráfico).

## 6. Componentes de UI (Frontend/Mobile)
- **PetForm**: Componente reutilizável para criar e editar. Seleção de espécie carrega raças dinamicamente.
- **PetCard**: Card com foto, nome e resumo, exibido na lista principal.
- **WeightChart**: Componente de gráfico gerado no frontend (Recharts para Web).
- **UploadButton**: Integração com câmera/galeria com compressão via canvas/image-resizer antes de enviar à API.

## 7. Critérios de Aceite
- [ ] Cadastro com nome e espécie obrigatórios (mínimo).
- [ ] Listagem de raças carregada via autocomplete pela espécie.
- [ ] Compressão local da foto do pet para <= 500KB.
- [ ] Arquivamento remove o pet da lista principal mas permite restauração.
- [ ] Bloqueio de criação do 3º pet para usuários Free.
