# 📊 Relatório de Conformidade — PetLife vs PRD v3.0
> **Gerado em:** 16/07/2026 | **Referência:** PRD v3.0 + Swagger `/v3/api-docs` (ao vivo) + Análise de código

---

## Legenda de Status

| Ícone | Significado |
|---|---|
| ✅ | Implementado e funcionando |
| ⚠️ | Implementado parcialmente ou com ressalvas |
| ❌ | Não implementado (gap) |
| 🔧 | Implementado mas com bug conhecido |
| 🚫 | Fora do escopo do MVP (roadmap futuro) |

---

## M01 — Autenticação e Conta

### Backend (API)
| Endpoint PRD | Endpoint Implementado | Status |
|---|---|---|
| `POST /auth/register` | `POST /api/v1/auth/register` | ✅ |
| `POST /auth/login` | `POST /api/v1/auth/login` | ✅ |
| `POST /auth/oauth/google` | `POST /api/v1/auth/google` ⚠️ (path diferente) | ✅ |
| `POST /auth/oauth/apple` | — | ❌ Não implementado |
| `GET /auth/me` | `GET /api/v1/auth/me` | ✅ |
| `PUT /auth/me` | `PUT /api/v1/auth/me` | ✅ |
| `POST /auth/me/photo` | `POST /api/v1/auth/me/photo` | ✅ |
| `POST /auth/forgot-password` | `POST /api/v1/auth/forgot-password` | ✅ |
| `POST /auth/reset-password` | `POST /api/v1/auth/reset-password` | ✅ |
| `POST /auth/refresh` | — | ❌ Não implementado |
| `DELETE /auth/account` | `DELETE /api/v1/auth/me` | ✅ |

### Frontend
| Funcionalidade | Status |
|---|---|
| Tela de Login | ✅ |
| Tela de Cadastro | ✅ |
| Recuperação de Senha (forgot-password) | ✅ |
| Reset de Senha (reset-password) | ✅ |
| Edição de Perfil (ProfilePage) | ✅ |
| Exclusão de Conta | ✅ (na ProfilePage) |
| Login com Google | ⚠️ Botão desabilitado no frontend |
| Login com Apple | ❌ Botão desabilitado |

### Gaps Identificados
> [!WARNING]
> - **`POST /auth/refresh`**: Token refresh não está implementado no backend. Quando o access token (15 min) expira, o usuário é deslogado sem chance de renovação automática.
> - **Apple Sign-In**: Não implementado em nenhuma camada (P0 no PRD para iOS).
> - **E-mail de verificação**: O backend não envia e-mail de verificação após o cadastro (PRD 7.1.4 exige confirmação em ≤ 30s).
> - **Rate limiting de login**: 5 tentativas/5min não está implementado (falta integração com Redis/Bucket4j).

---

## M02 — Gestão de Pets

### Backend (API)
| Endpoint PRD | Endpoint Implementado | Status |
|---|---|---|
| `GET /pets` | `GET /api/v1/pets` (paginado, filtro por status) | ✅ |
| `POST /pets` | `POST /api/v1/pets` | ✅ |
| `GET /pets/:id` | `GET /api/v1/pets/{id}` | ✅ |
| `PUT /pets/:id` | `PUT /api/v1/pets/{id}` | ✅ |
| `PATCH /pets/:id/archive` | `PATCH /api/v1/pets/{id}/status` (mais genérico) | ✅ |
| `DELETE /pets/:id` | — | ❌ Não implementado |
| `GET /pets/:id/timeline` | `GET /api/v1/pets/{petId}/timeline` | ✅ |
| `GET /pets/:id/report/pdf` | `GET /api/v1/pets/{petId}/export` (retorna bytes) | ⚠️ |
| `POST /pets/:id/photo` | `POST /api/v1/pets/{id}/photo` | ✅ |
| `GET /pets/:id/weight-history` | `GET /api/v1/pets/{id}/weight-history` | ✅ |

### Frontend
| Funcionalidade | Status |
|---|---|
| Cadastro de Pet (PetFormPage) | ✅ |
| Edição de Pet (PetProfilePage) | ✅ |
| Upload de Foto | 🔧 Corrigido hoje (Base64) — aguardar validação |
| Arquivamento de Pet | ✅ |
| Múltiplos Pets (listagem) | ✅ |
| Exclusão de Pet | ⚠️ Botão existe mas backend sem endpoint DELETE |
| Gráfico de Peso | ✅ (PetProfilePage tem gráfico via weight-history) |

### Gaps Identificados
> [!CAUTION]
> - **`DELETE /pets/:id`**: Endpoint de exclusão de pet **não existe no backend**. O botão de excluir pet no frontend não funciona, o que é violação crítica de LGPD (Art. 18, VI — direito à eliminação).
> - **Foto do pet**: Coluna `photo_url` foi alterada para `TEXT` hoje (migration V16). Aguarda validação em produção.
> - **Exportação PDF**: O endpoint `/pets/{petId}/export` retorna bytes, mas não há link no frontend para exportar PDF — funcionalidade inacessível ao usuário.

---

## M03 — Vacinação

### Backend (API)
| Endpoint PRD | Endpoint Implementado | Status |
|---|---|---|
| `GET /pets/:petId/vaccinations` | `GET /api/v1/pets/{petId}/vaccines` ⚠️ (path: vaccines vs vaccinations) | ✅ |
| `POST /pets/:petId/vaccinations` | `POST /api/v1/pets/{petId}/vaccines` | ✅ |
| `PUT /pets/:petId/vaccinations/:id` | `PUT /api/v1/pets/{petId}/vaccines/{vaccineId}` | ✅ |
| `DELETE /pets/:petId/vaccinations/:id` | — | ❌ Não implementado |
| Upload de comprovante | `POST /api/v1/pets/{petId}/vaccines/{vaccineId}/proof` | ✅ |
| Sugestões de vacinas | `GET /api/v1/vaccines/suggestions?species=` | ✅ |

### Frontend
| Funcionalidade | Status |
|---|---|
| Listagem de vacinas | ✅ (PetProfilePage / AppointmentsPage) |
| Cadastro de vacina | ✅ |
| Upload de comprovante | ⚠️ API existe, mas o frontend não tem campo para upload de comprovante no formulário |
| Sugestões autocomplete | ⚠️ API existe (`/vaccines/suggestions`) mas não está integrada no formulário frontend |
| Exclusão de vacina | ❌ Sem endpoint no backend |

### Gaps Identificados
> [!WARNING]
> - **DELETE de vacina** ausente no backend.
> - **Autocomplete de vacinas** por espécie: API implementada (`/vaccines/suggestions`) mas o frontend do formulário de vacina não faz chamada a esse endpoint.
> - **Upload de comprovante** de vacina: API existe mas não há UI para isso.

---

## M04 — Consultas Veterinárias

### Backend (API)
| Endpoint PRD | Endpoint Implementado | Status |
|---|---|---|
| `GET /pets/:petId/consultations` | `GET /api/v1/pets/{petId}/consultations` | ✅ |
| `POST /pets/:petId/consultations` | `POST /api/v1/pets/{petId}/consultations` | ✅ |
| `PUT /pets/:petId/consultations/:id` | — | ❌ Não implementado |
| `DELETE /pets/:petId/consultations/:id` | — | ❌ Não implementado |
| Upload de anexos | `POST /api/v1/pets/{petId}/consultations/{id}/attachments` | ✅ |
| Delete de anexo | `DELETE /api/v1/pets/{petId}/consultations/{id}/attachments/{index}` | ✅ |

### Frontend
| Funcionalidade | Status |
|---|---|
| Listagem de consultas | ✅ |
| Cadastro de consulta | ✅ |
| Upload de anexos (exames) | ⚠️ API existe, UI parcial |
| Agendamento de retorno | ⚠️ Campo `followUpDate` existe no formulário, mas não gera notificação no backend |
| Atualização/Exclusão de consulta | ❌ Sem endpoints no backend |

### Gaps Identificados
> [!WARNING]
> - **PUT e DELETE de consulta** ausentes no backend.
> - O campo `reason` na `CreateConsultationRequest` não está marcado como obrigatório apesar do PRD exigir.
> - O peso registrado na consulta (`weightAtVisit`) **não atualiza automaticamente** o `weightKg` do pet (regra de negócio do PRD 7.4.3).

---

## M05 — Controle de Medicamentos

### Backend (API)
| Endpoint PRD | Endpoint Implementado | Status |
|---|---|---|
| `GET /pets/:petId/medications` | `GET /api/v1/pets/{petId}/medications` | ✅ |
| `POST /pets/:petId/medications` | `POST /api/v1/pets/{petId}/medications` | ✅ |
| `PUT /pets/:petId/medications/:id` | — | ❌ Não implementado |
| `PATCH /.../medications/:id/close` | `PATCH /api/v1/medications/{id}/stop` | ✅ |
| Marcar dose | `PATCH /api/v1/medications/doses/{doseId}` | ✅ |
| Aderência | `GET /api/v1/pets/{petId}/medications/adherence` | ✅ |

### Frontend
| Funcionalidade | Status |
|---|---|
| Listagem de medicamentos | ✅ |
| Cadastro com horários | ✅ |
| **Duração em dias** | ❌ Reclamação do usuário: não há campo de duração em dias no formulário |
| Marcar dose como tomada/pulada | ✅ |
| Aderência em % | ✅ (Medicações página) |
| Encerrar tratamento | ✅ |
| Edição de medicamento | ❌ Sem endpoint no backend |

### Gaps Identificados
> [!CAUTION]
> - **Duração em dias**: O PRD define campo `end_date`. O `CreateMedicationRequest` tem `endDate` mas o frontend não expõe claramente um campo de "por quantos dias" — o usuário reclamou deste problema. O formulário usa `startDate` + `endDate` mas a UX não está clara.
> - **PUT de medicamento** ausente no backend.
> - **Campos `prescribedBy` e `reason`** existem no modelo do PRD mas estão ausentes do `CreateMedicationRequest` atual.

---

## M06 — Banho e Tosa

### Backend (API)
| Endpoint PRD | Endpoint Implementado | Status |
|---|---|---|
| `GET /pets/:petId/groomings` | `GET /api/v1/pets/{petId}/groomings` | ✅ |
| `POST /pets/:petId/groomings` | `POST /api/v1/pets/{petId}/groomings` | ✅ |
| `PUT /pets/:petId/groomings/:id` | `PUT /api/v1/pets/{petId}/groomings/{id}` | ✅ |
| `DELETE /pets/:petId/groomings/:id` | — | ❌ Não implementado |
| Upload de fotos antes/depois | `POST /api/v1/pets/{petId}/groomings/{id}/photos?type=before|after` | ✅ |

### Frontend
| Funcionalidade | Status |
|---|---|
| Listagem de procedimentos | ✅ (GroomingPage) |
| Cadastro com periodicidade | ✅ |
| Fotos antes/depois | ✅ (BeforeAfterViewer) |
| Menu lateral/navegação | ✅ (sidebar + acesso rápido) |
| Tela branca ao navegar | 🔧 Corrigido hoje (guard de loading) |
| Exclusão de procedimento | ❌ Sem endpoint no backend |

---

## M07 — Linha do Tempo

### Backend (API)
| Endpoint PRD | Endpoint Implementado | Status |
|---|---|---|
| `GET /pets/:id/timeline` | `GET /api/v1/pets/{petId}/timeline` (paginado, filtro por tipos) | ✅ |

> A timeline consolida: VACCINE, CONSULTATION, MEDICATION_START, MEDICATION_END, GROOMING, PHOTO, WEIGHT, BIRTHDAY.

### Frontend
| Funcionalidade | Status |
|---|---|
| MemoriesPage (linha do tempo) | ✅ Existe como "Diário de Memórias" |
| Filtros por tipo | ⚠️ UI existe mas integração com API needs verification |
| Paginação / scroll infinito | ⚠️ API suporta paginação mas UI pode não implementar |
| Link para detalhe do evento | ⚠️ Parcial |

---

## M08 — Notificações

### Backend (API)
| Endpoint PRD | Endpoint Implementado | Status |
|---|---|---|
| `GET /notifications` | `GET /api/v1/notifications` (paginado) | ✅ |
| `PATCH /notifications/:id/read` | `PATCH /api/v1/notifications/{id}/read` | ✅ |
| `PATCH /notifications/read-all` | `PATCH /api/v1/notifications/read-all` | ✅ |
| `PUT /notifications/settings` | `PUT /api/v1/users/me/notification-preferences` | ✅ |
| Registro de device token (FCM) | `POST /api/v1/users/me/device-tokens` | ✅ |

### Frontend
| Funcionalidade | Status |
|---|---|
| Central de Notificações (NotificationsPage) | ✅ |
| Marcar como lida | ✅ |
| Preferências de notificação | ⚠️ Existe no backend; verificar se há tela no frontend |
| Registro de FCM token | ❌ Frontend não faz chamada ao endpoint de device-token |

### Gaps Identificados
> [!WARNING]
> - **Push Notifications reais (FCM)**: O backend tem endpoint para registrar device token, mas o **frontend não registra o FCM token** em nenhum momento. Notificações push nunca serão entregues.
> - **Scheduler de notificações**: O backend pode ter `@Scheduled` para criar notificações in-app, mas sem FCM configurado não haverá push real.

---

## M09 — Cadastro e Busca de Veterinários

### Backend (API)
| Endpoint PRD | Endpoint Implementado | Status |
|---|---|---|
| `POST /veterinarians` | `POST /api/v1/veterinarians` | ✅ |
| `GET /veterinarians/me` | — | ❌ Não implementado |
| `PUT /veterinarians/me` | — | ❌ Não implementado |
| `PATCH /veterinarians/me/availability` | `PATCH /api/v1/veterinarians/availability` (sem /me/) | ✅ |
| `PATCH /veterinarians/me/emergency` | Consolidado em `PATCH /availability` (campo `emergencyOnDuty`) | ✅ |
| `POST /veterinarians/me/addresses` | `POST /api/v1/veterinarians/address` | ✅ |
| `PUT /veterinarians/me/addresses/:id` | — | ❌ Não implementado |
| `DELETE /veterinarians/me/addresses/:id` | — | ❌ Não implementado |
| `POST /veterinarians/me/schedules` | `POST /api/v1/veterinarians/schedule` | ✅ |
| `PUT /veterinarians/me/schedules/:id` | — | ❌ Não implementado |
| `DELETE /veterinarians/me/schedules/:id` | — | ❌ Não implementado |
| `GET /veterinarians/search` | `GET /api/v1/veterinarians/search` | ✅ |
| `GET /veterinarians/:id` | `GET /api/v1/veterinarians/{veterinarianId}` | ✅ |
| `POST /veterinarians/:id/favorite` | `POST /api/v1/veterinarians/{veterinarianId}/favorite` (toggle) | ✅ |
| `DELETE /veterinarians/:id/favorite` | Consolidado no toggle acima | ✅ |
| `GET /veterinarians/favorites` | — | ❌ Não implementado |

### Frontend
| Funcionalidade | Status |
|---|---|
| Busca de Veterinários (VetSearchPage) | ✅ |
| Filtros (espécie, modalidade, emergência) | ✅ |
| Detalhes do Vet (VetDetailPage) | ✅ |
| Favoritar Veterinário | ✅ |
| Lista de Favoritos (VetFavoritesPage) | ✅ (página existe) |
| Perfil do Veterinário (para o vet se cadastrar) | ✅ (VetProfilePage existe) |
| Filtro por plano/convênio | ⚠️ Filtro de `paymentTypes` no Swagger mas UI pode não ter |
| Filtro por proximidade (geolocalização) | ⚠️ API suporta `lat/lng` mas frontend precisa solicitar permissão de localização |

### Gaps Identificados
> [!WARNING]
> - **GET/PUT `/veterinarians/me`**: Um vet logado não tem endpoint para visualizar/editar seu próprio perfil.
> - **Gerenciamento de endereços**: Não há PUT nem DELETE para endereços de veterinários.
> - **Gerenciamento de horários**: Não há PUT nem DELETE para horários.
> - **Lista de favoritos** (`GET /veterinarians/favorites`): Endpoint ausente no backend — a tela `VetFavoritesPage` provavelmente não funciona.

---

## Extras Implementados (não previstos no PRD)

| Funcionalidade | Onde | Observação |
|---|---|---|
| Rotina / Atividades (`RoutineActivity`) | Backend + Frontend | Adicionado como extensão do MVP. Tipos: WALK, FEEDING, GENERIC. Agendamento Vet usa `GROOMING` ou `GENERIC`. |
| Onboarding (4 telas) | Frontend | Telas de apresentação para novos usuários |
| `GET /vaccines/suggestions?species=` | Backend | Sugestões por espécie (M03 do PRD) — API implementada mas frontend não consome |
| Exportação de dados (`/pets/{petId}/export`) | Backend | Retorna bytes (PDF/JSON). Frontend sem link de acesso |

---

## Análise do Swagger vs PRD — Endpoints Ausentes Críticos

| Endpoint Faltante | Módulo | Impacto |
|---|---|---|
| `DELETE /api/v1/pets/{id}` | M02 | 🔴 LGPD — exclusão em cascata |
| `DELETE /api/v1/pets/{petId}/vaccines/{id}` | M03 | 🟡 Usuário não pode corrigir vacinas erradas |
| `PUT /api/v1/pets/{petId}/consultations/{id}` | M04 | 🟡 Consultas imutáveis após salvar |
| `DELETE /api/v1/pets/{petId}/consultations/{id}` | M04 | 🟡 LGPD |
| `PUT /api/v1/pets/{petId}/medications/{id}` | M05 | 🟡 Medicamentos imutáveis |
| `DELETE /api/v1/pets/{petId}/groomings/{id}` | M06 | 🟡 |
| `POST /api/v1/auth/refresh` | M01 | 🔴 Sessão expira sem renovação |
| `GET /api/v1/veterinarians/me` | M09 | 🟡 Vet não consegue ver seu próprio perfil |
| `GET /api/v1/veterinarians/favorites` | M09 | 🟡 Tela de favoritos quebrada |

---

## Bugs Conhecidos e Pendentes

| Bug | Módulo | Status |
|---|---|---|
| Foto do pet corrompida (URL fake S3) | M02 | 🔧 Corrigido hoje — Base64 + migração V16 |
| Tela branca em Banho e Tosa | M06 | 🔧 Corrigido hoje — guard de loading |
| Botão "Banho e Tosa" redirecionando errado | M06 | ✅ Corrigido em sessão anterior |
| Agendamento veterinário sem persistência | M04 | ⚠️ Verificar fluxo de `followUpDate` |
| Duração em dias do medicamento sem UX clara | M05 | ⚠️ Campo `endDate` existe, UX confusa |
| Autocomplete de vacinas não conectado | M03 | ⚠️ API existe, frontend não consome |
| FCM token não registrado | M08 | ❌ Push real nunca disparado |

---

## Conformidade Geral por Módulo

| Módulo | Backend | Frontend | Cobertura Estimada |
|---|---|---|---|
| M01 — Autenticação | 85% | 90% | **87%** |
| M02 — Pets | 80% | 85% | **82%** |
| M03 — Vacinação | 75% | 70% | **72%** |
| M04 — Consultas | 70% | 80% | **75%** |
| M05 — Medicamentos | 80% | 75% | **77%** |
| M06 — Banho e Tosa | 85% | 90% | **87%** |
| M07 — Linha do Tempo | 90% | 75% | **82%** |
| M08 — Notificações | 85% | 70% | **77%** |
| M09 — Veterinários | 70% | 85% | **77%** |
| **GERAL** | **80%** | **80%** | **80%** |

---

## Prioridades de Correção (Sugestão)

### 🔴 Alta Prioridade (bloqueadores de produção)
1. Implementar `DELETE /pets/{id}` (LGPD crítico)
2. Implementar `POST /auth/refresh` (sessão expira sem renovação)
3. Registrar FCM token no frontend e configurar Firebase (push real)
4. Adicionar duração em dias (endDate calculado) com UX clara nos Medicamentos
5. Implementar `GET /veterinarians/favorites` no backend

### 🟡 Média Prioridade
6. Implementar DELETE para vacinas, consultas, groomings
7. Implementar PUT para consultas e medicamentos  
8. Conectar autocomplete de vacinas por espécie no formulário frontend
9. Adicionar UI para upload de comprovante de vacina
10. Implementar `GET/PUT /veterinarians/me`
11. Implementar gerenciamento (PUT/DELETE) de endereços e horários de vets
12. Exibir link para exportar PDF do pet no frontend

### 🟢 Baixa Prioridade (melhoria de UX)
13. Apple Sign-In (crítico para iOS, mas sem prazo imediato)
14. E-mail de verificação após cadastro
15. Rate limiting de login
16. Conectar preferências de notificação ao frontend

---

> **Observação:** Análise baseada no Swagger ao vivo (`http://localhost:8081/v3/api-docs`) que reflete o estado atual da aplicação em desenvolvimento. O Swagger confirma 47 endpoints implementados dos ~65 previstos no PRD (72% de cobertura de API).
