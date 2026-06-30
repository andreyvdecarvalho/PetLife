# ADR 005: Conformidade com LGPD — Estratégia de Cascade Delete vs Soft-Delete

## Status
Aprovado

## Data
30 de Junho de 2026

## Contexto
O PetLife coleta e armazena dados pessoais dos tutores (nomes, e-mails, senhas, telefones, fuso horário) e dados sensíveis ou comerciais que caem sob a alçada da **LGPD (Lei Geral de Proteção de Dados)**. Conforme o Artigo 18 da LGPD, o titular dos dados pessoais tem o direito de solicitar a eliminação dos dados tratados com o seu consentimento (Direito ao Esquecimento).

Por outro lado, o arquivamento ou desativação acidental de um pet ou tratamento médico não deve apagar fisicamente o histórico de forma imediata do banco de dados, para que o tutor possa desfazer a ação ou manter registros históricos (auditoria clínica) sem que isso configure violação de dados, desde que o tutor mantenha a conta ativa.

## Decisão
Decidimos implementar uma estratégia híbrida de manipulação de exclusão de dados:

1. **Exclusão Física em Cascata (Cascade Delete) para Contas de Usuários**:
   Quando um usuário solicita a exclusão de sua conta (`DELETE /api/v1/auth/account`), o sistema realiza uma exclusão física e permanente de todos os seus dados e de todas as entidades dependentes no banco (pets, vacinas, diagnósticos, tratamentos, exames) em até 72 horas.
   - Configurado no Hibernate/JPA do backend via `@OneToMany(mappedBy = "...", cascade = CascadeType.REMOVE, orphanRemoval = true)`.
   - Garante que nenhum dado pessoal ou residual do tutor permaneça nos servidores de forma ilegal após a exclusão da conta.
2. **Soft-Delete (Arquivamento) para Domínios de Uso Clínico e Histórico**:
   Para exclusões de entidades que não representam o encerramento da conta do usuário (como remover um pet ou finalizar um tratamento de medicação), o sistema utiliza **soft-delete** ou controle de estados (`pet_status` como `ARCHIVED` ou `ACTIVE`).
   - Evita a perda acidental de históricos médicos importantes dos pets que o usuário possa querer reativar.
   - Dados soft-deletados permanecem no banco de dados, mas não são retornados nas buscas padrões da aplicação.
   - Quando o usuário deletar a conta definitivamente (US-005), os registros em estado `ARCHIVED` serão fisicamente deletados devido à regra 1.

## Consequências
### Positivas:
- Conformidade estrita com o Artigo 18 da LGPD no que tange à eliminação de dados de usuários.
- Prevenção contra perda de dados acidental (erro humano) em históricos médicos e perfis de pets por meio de soft-delete.
- Banco de dados limpo e sem resíduos órfãos após exclusões de conta devido ao Cascade Delete físico.

### Negativas / Custos:
- Risco de sobrecarga de banco de dados se houver exclusões em cascata massivas de usuários simultâneos (mitigado pelo volume inicial e arquitetura do Postgres).
- Complexidade na escrita de queries SQL que devem desconsiderar registros marcados como `ARCHIVED`/deletados logicamente na rotina comum.
