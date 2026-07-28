# Spec Driven Development (SDD-002): Completude de CRUD

> **Status:** ✅ Concluído | **Prioridade:** P1 — Features Incompletas
> **Responsável:** Agente Implementador | **Sprint:** 2–3 (Semanas 2–4)
> **Débitos cobertos:** DB-08 a DB-23

## Objetivo
Fechar 16 gaps de CRUD identificados no `docs/compliance-report.md`:
- DELETEs ausentes: vacinas, consultas, banho/tosa
- PUTs ausentes: consultas, medicamentos
- Gestão completa de perfil do veterinário
- Conectar funcionalidades de frontend a APIs existentes

**Leia antes de implementar:**
- `docs/DOD.md` — critérios de qualidade
- `docs/PRD.md` seções 7.3 (vacinas), 7.4 (consultas), 7.5 (medicamentos), 7.6 (banho/tosa), 7.9 (veterinários)
- `GEMINI.md` seção 12 — Regras de Ouro
- `.gemini/skills/hexagonal-backend/SKILL.md` — templates de código

---

## 1. FASE 1 — DELETEs Pendentes (DB-08, DB-12, DB-18)

> Padrão de implementação igual para todos os 3 módulos:
> Port → Adapter → UseCase → Controller → Frontend

### 1.1 DB-08: DELETE /pets/{petId}/vaccines/{id}

#### [MODIFY] Port de Vacinação
Verificar se `VaccinationRepositoryPort` tem `deleteById`. Se não:
```java
void deleteById(UUID vaccinationId);
boolean existsByIdAndPetId(UUID vaccinationId, UUID petId);
```

#### [NEW] `DeleteVaccinationUseCase.java`
```java
package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.domain.port.VaccinationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeleteVaccinationUseCase {

    private final VaccinationRepositoryPort vaccinationRepository;

    public void execute(UUID petId, UUID vaccinationId, UUID requestingUserId) {
        if (!vaccinationRepository.existsByIdAndPetId(vaccinationId, petId)) {
            throw new IllegalArgumentException("Vacinação não encontrada para o pet informado");
        }
        vaccinationRepository.deleteById(vaccinationId);
    }
}
```

#### [MODIFY] Controller de Vacinação
```java
@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void delete(
        @PathVariable UUID petId,
        @PathVariable UUID id,
        @AuthenticationPrincipal Jwt jwt) {
    UUID userId = UUID.fromString(jwt.getSubject());
    deleteVaccinationUseCase.execute(petId, id, userId);
}
```

#### [MODIFY] `apps/web/src/infrastructure/http/vaccination.api.ts`
```typescript
delete: async (petId: string, vaccinationId: string): Promise<void> => {
  await api.delete(`/pets/${petId}/vaccines/${vaccinationId}`);
},
```

### 1.2 DB-12: DELETE /pets/{petId}/consultations/{id}
Mesmo padrão acima. Atenção: Se houver anexos (attachments), deletar via S3 ou MinIO antes de deletar o registro.

```java
// DeleteConsultationUseCase — verificar se existe FileStoragePort
// Se verificar: fileStoragePort.deleteConsultationAttachments(consultationId)
// Depois: consultationRepository.deleteById(consultationId)
```

### 1.3 DB-18: DELETE /pets/{petId}/groomings/{id}
Mesmo padrão. Verificar se grooming tem fotos antes/depois:
```java
// DeleteGroomingUseCase — verificar se há fotos vinculadas
// Se existir GroomingPhotoRepositoryPort: deletar fotos antes
```

---

## 2. FASE 2 — PUT Consultas (DB-11, DB-13, DB-14)

### 2.1 DB-11: PUT /pets/{petId}/consultations/{id}

#### [NEW] `UpdateConsultationUseCase.java`
```java
package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.domain.entity.Consultation;
import com.petlife.modules.pet.domain.port.ConsultationRepositoryPort;
import com.petlife.modules.pet.domain.port.PetRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UpdateConsultationUseCase {

    private final ConsultationRepositoryPort consultationRepository;
    private final PetRepositoryPort petRepository; // DB-13: atualiza peso do pet

    public Consultation execute(
            UUID petId,
            UUID consultationId,
            String reason,         // DB-14: obrigatório
            String diagnosis,
            String notes,
            String veterinarianName,
            BigDecimal weightAtVisit, // DB-13
            LocalDate visitDate) {

        Consultation consultation = consultationRepository.findById(consultationId)
            .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada"));

        if (!consultation.getPetId().equals(petId)) {
            throw new SecurityException("Consulta não pertence ao pet informado");
        }

        consultation.setReason(reason);
        consultation.setDiagnosis(diagnosis);
        consultation.setNotes(notes);
        consultation.setVeterinarianName(veterinarianName);
        consultation.setWeightAtVisit(weightAtVisit);
        consultation.setVisitDate(visitDate);

        Consultation updated = consultationRepository.save(consultation);

        // DB-13: Atualizar peso do pet automaticamente se peso fornecido
        if (weightAtVisit != null) {
            petRepository.findById(petId).ifPresent(pet -> {
                pet.setWeightKg(weightAtVisit);
                petRepository.save(pet);
            });
        }

        return updated;
    }
}
```

#### [NEW] `UpdateConsultationRequest.java`
```java
package com.petlife.modules.pet.infrastructure.dto;

import jakarta.validation.constraints.NotBlank; // DB-14: reason obrigatório
import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateConsultationRequest(
    @NotBlank(message = "Motivo da consulta é obrigatório") // DB-14
    String reason,
    String diagnosis,
    String notes,
    String veterinarianName,
    BigDecimal weightAtVisit, // DB-13
    LocalDate visitDate
) {}
```

#### [MODIFY] `CreateConsultationRequest.java`
Tornar `reason` obrigatório (DB-14):
```java
// Adicionar/atualizar anotação:
@NotBlank(message = "Motivo da consulta é obrigatório")
String reason,
```

#### [MODIFY] Controller de Consulta — endpoint PUT
```java
@PutMapping("/{id}")
public ResponseEntity<ApiResponse<ConsultationResponse>> update(
        @PathVariable UUID petId,
        @PathVariable UUID id,
        @Valid @RequestBody UpdateConsultationRequest request,
        @AuthenticationPrincipal Jwt jwt) {
    Consultation updated = updateConsultationUseCase.execute(
        petId, id,
        request.reason(), request.diagnosis(), request.notes(),
        request.veterinarianName(), request.weightAtVisit(), request.visitDate()
    );
    return ResponseEntity.ok(ApiResponse.success(consultationMapper.toResponse(updated)));
}
```

### 2.2 Frontend — Formulário de Edição de Consulta

#### [MODIFY] `apps/web/src/infrastructure/http/consultation.api.ts`
```typescript
update: async (
  petId: string,
  consultationId: string,
  payload: UpdateConsultationRequest
): Promise<ConsultationResponse> => {
  const { data } = await api.put(`/pets/${petId}/consultations/${consultationId}`, payload);
  return data.data;
},
```

#### [MODIFY] `apps/web/src/components/organisms/ConsultationForm/index.tsx`
Adicionar suporte a edição (receber `initialData?: ConsultationResponse`):
- Se `initialData` presente: modo edição → chamar `consultationApi.update()`
- Se não: modo criação → chamar `consultationApi.create()`

---

## 3. FASE 3 — PUT Medicamentos (DB-15, DB-16, DB-17)

### 3.1 DB-15: PUT /pets/{petId}/medications/{id}

#### [NEW] `UpdateMedicationUseCase.java`
```java
package com.petlife.modules.medication.application.usecase;

import com.petlife.modules.medication.domain.entity.Medication;
import com.petlife.modules.medication.domain.port.MedicationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UpdateMedicationUseCase {

    private final MedicationRepositoryPort medicationRepository;

    public Medication execute(
            UUID petId,
            UUID medicationId,
            String name,
            String dosage,
            String frequency,
            LocalDate startDate,
            LocalDate endDate,
            String prescribedBy, // DB-17
            String reason) {    // DB-17

        Medication medication = medicationRepository.findById(medicationId)
            .orElseThrow(() -> new IllegalArgumentException("Medicamento não encontrado"));

        if (!medication.getPetId().equals(petId)) {
            throw new SecurityException("Medicamento não pertence ao pet informado");
        }

        medication.setName(name);
        medication.setDosage(dosage);
        medication.setFrequency(frequency);
        medication.setStartDate(startDate);
        medication.setEndDate(endDate);
        medication.setPrescribedBy(prescribedBy); // DB-17
        medication.setReason(reason);             // DB-17

        return medicationRepository.save(medication);
    }
}
```

#### [MODIFY] Migration — Adicionar colunas se não existirem (DB-17)
#### [NEW] `V{N}__add_medication_prescription_fields.sql`
```sql
ALTER TABLE medications
    ADD COLUMN IF NOT EXISTS prescribed_by VARCHAR(255),
    ADD COLUMN IF NOT EXISTS reason VARCHAR(500);
```

### 3.2 DB-16: UX de Duração em Dias

#### [MODIFY] `apps/web/src/components/organisms/MedicationForm` (ou equivalente)
Substituir dois campos (`startDate` + `endDate`) por:
- `startDate` (date picker)
- `durationDays` (number input)
- `endDate` calculado automaticamente: `endDate = startDate + durationDays`

```typescript
// Lógica no formulário:
const handleStartDateOrDuration = () => {
  if (startDate && durationDays > 0) {
    const end = new Date(startDate);
    end.setDate(end.getDate() + Number(durationDays));
    setValue('endDate', end.toISOString().split('T')[0]);
  }
};
```

---

## 4. FASE 4 — Perfil Veterinário (DB-19, DB-20, DB-21)

### 4.1 DB-19: GET e PUT /veterinarians/me

#### [NEW] `GetMyVeterinarianProfileUseCase.java`
```java
package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.veterinarian.domain.entity.Veterinarian;
import com.petlife.modules.veterinarian.domain.port.VeterinarianRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetMyVeterinarianProfileUseCase {

    private final VeterinarianRepositoryPort veterinarianRepository;

    public Veterinarian execute(UUID userId) {
        return veterinarianRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalStateException(
                "Perfil de veterinário não encontrado para o usuário: " + userId));
    }
}
```

#### [MODIFY] `VeterinarianController.java` — adicionar endpoints `/me`
```java
@GetMapping("/me")
public ResponseEntity<ApiResponse<VeterinarianResponse>> getMyProfile(
        @AuthenticationPrincipal Jwt jwt) {
    UUID userId = UUID.fromString(jwt.getSubject());
    Veterinarian vet = getMyVeterinarianProfileUseCase.execute(userId);
    return ResponseEntity.ok(ApiResponse.success(vetMapper.toResponse(vet)));
}

@PutMapping("/me")
public ResponseEntity<ApiResponse<VeterinarianResponse>> updateMyProfile(
        @Valid @RequestBody UpdateVeterinarianRequest request,
        @AuthenticationPrincipal Jwt jwt) {
    UUID userId = UUID.fromString(jwt.getSubject());
    Veterinarian updated = updateVeterinarianProfileUseCase.execute(userId, request);
    return ResponseEntity.ok(ApiResponse.success(vetMapper.toResponse(updated)));
}
```

### 4.2 DB-20: PUT/DELETE Endereços do Veterinário

#### [MODIFY] `VeterinarianController.java`
```java
@PutMapping("/me/addresses/{addressId}")
public ResponseEntity<ApiResponse<VetAddressResponse>> updateAddress(
        @PathVariable UUID addressId,
        @Valid @RequestBody UpdateVetAddressRequest request,
        @AuthenticationPrincipal Jwt jwt) {
    UUID userId = UUID.fromString(jwt.getSubject());
    // updateVetAddressUseCase.execute(userId, addressId, request)
    ...
}

@DeleteMapping("/me/addresses/{addressId}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void deleteAddress(
        @PathVariable UUID addressId,
        @AuthenticationPrincipal Jwt jwt) {
    UUID userId = UUID.fromString(jwt.getSubject());
    // deleteVetAddressUseCase.execute(userId, addressId)
    ...
}
```

### 4.3 DB-21: PUT/DELETE Horários do Veterinário

Mesmo padrão de DB-20 para `/me/schedules/{scheduleId}`.

---

## 5. FASE 5 — Conexões Frontend Pendentes (DB-09, DB-10, DB-22, DB-23)

### 5.1 DB-09: Autocomplete de Vacinas por Espécie

#### [MODIFY] `apps/web/src/infrastructure/http/vaccination.api.ts`
```typescript
// A API já existe no backend: GET /vaccines/suggestions?species={species}
getSuggestions: async (species: string): Promise<string[]> => {
  const { data } = await api.get('/vaccines/suggestions', { params: { species } });
  return data.data;
},
```

#### [MODIFY] `apps/web/src/components/organisms/VaccineForm/index.tsx`
Adicionar datalist com sugestões dinâmicas:
```typescript
// Hook para buscar sugestões quando espécie mudar
const [suggestions, setSuggestions] = useState<string[]>([]);
const petSpecies = watch('species') ?? initialPetSpecies;

useEffect(() => {
  if (petSpecies) {
    vaccinationApi.getSuggestions(petSpecies)
      .then(setSuggestions)
      .catch(() => setSuggestions([]));
  }
}, [petSpecies]);

// No JSX — Input com datalist
<input
  id="vaccine-name"
  list="vaccine-suggestions"
  {...register('name')}
/>
<datalist id="vaccine-suggestions">
  {suggestions.map(s => <option key={s} value={s} />)}
</datalist>
```

### 5.2 DB-10: Upload de Comprovante de Vacina

#### [MODIFY] `apps/web/src/infrastructure/http/vaccination.api.ts`
```typescript
// A API já existe: POST /pets/{petId}/vaccines/{id}/proof
uploadProof: async (petId: string, vaccinationId: string, file: File): Promise<void> => {
  const formData = new FormData();
  formData.append('file', file);
  await api.post(`/pets/${petId}/vaccines/${vaccinationId}/proof`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
},
```

#### [MODIFY] `apps/web/src/components/organisms/VaccineForm/index.tsx`
Adicionar componente `ProofUploader` após salvar vacina com sucesso:
```typescript
// Importar molecule existente:
import { ProofUploader } from '../../molecules/ProofUploader';

// Após save bem-sucedido — mostrar uploader opcional:
{savedVaccinationId && (
  <ProofUploader
    label="Adicionar comprovante (opcional)"
    onUpload={(file) => vaccinationApi.uploadProof(petId, savedVaccinationId, file)}
  />
)}
```

### 5.3 DB-22: Link de Export PDF

#### [MODIFY] `apps/web/src/infrastructure/http/pet.api.ts`
```typescript
// A API já existe: GET /pets/{id}/export
exportMedicalPass: async (petId: string): Promise<Blob> => {
  const response = await api.get(`/pets/${petId}/export`, {
    responseType: 'blob',
    headers: { Accept: 'application/pdf' },
  });
  return response.data;
},
```

#### [NEW] `apps/web/src/infrastructure/browser/downloadFile.ts`
```typescript
export function downloadBlob(blob: Blob, filename: string): void {
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
}
```

#### [MODIFY] Tela de perfil do pet (página ou organism)
Adicionar botão "Exportar Carteira de Saúde":
```typescript
const handleExportPdf = async () => {
  try {
    const blob = await petApi.exportMedicalPass(petId);
    downloadBlob(blob, `carteira-${pet.name}.pdf`);
  } catch {
    toast.error('Erro ao gerar PDF');
  }
};

// JSX:
<Button label="Exportar Carteira de Saúde" onClick={handleExportPdf} variant="secondary" />
```

### 5.4 DB-23: Tela de Preferências de Notificação

#### [NEW] `apps/web/src/components/organisms/NotificationPreferencesForm/index.tsx`
Formulário com toggles para:
- Vacinas (lembretes X dias antes)
- Medicamentos (horário da dose)
- Consultas (lembretes)
- Peso (lembrete semanal/mensal)

#### [NEW] Rota `/settings/notifications`
Adicionar em `App.tsx` ou roteamento principal:
```typescript
<Route path="/settings/notifications" element={<NotificationPreferencesPage />} />
```

---

## 6. Regras Preventivas (SDD-002)

| # | Regra | Erro que previne |
|---|---|---|
| P1 | **Verificar FKs antes de implementar DELETE.** Se tabela não tem `ON DELETE CASCADE`, o UseCase DEVE deletar filhos manualmente ou a migration deve ser criada primeiro. | FK violation em runtime |
| P2 | **DB-13: Atualizar peso do pet na mesma transação da consulta.** Se transação falhar, peso não deve ser atualizado. Usar `@Transactional` no Adapter. | Inconsistência de dados |
| P3 | **DB-14: `reason` obrigatório no CREATE e UPDATE.** Validar em ambas as rotas. | Dados incompletos em produção |
| P4 | **DB-17: Verificar se coluna existe antes da migration.** Usar `ADD COLUMN IF NOT EXISTS`. | Migration falha se coluna já existir |
| P5 | **DB-22: Download de PDF via BrowserAdapter.** Nunca manipular DOM diretamente em hooks. | Violação de separação de camadas |
| P6 | **DB-09: Sugestões de vacinas são apenas sugestões.** Nunca bloquear submit se a API falhar. | UX quebrada por falha de rede |

---

## 7. Gate Check — SDD-002

```bash
cd apps/backend

# Compilação
mvn clean compile

# Verificações arquiteturais
grep -rn "ApiResponse" src/main/java/com/petlife/modules/*/application/
grep -rn "JpaRepository\|JpaEntity" src/main/java/com/petlife/modules/*/application/
grep -rL "^package " src/main/java/ --include="*.java"

# Testes (gate 85%)
mvn test

cd ../web
pnpm lint && pnpm typecheck && pnpm test
```

## 8. Atualizar após concluir

- [x] `docs/compliance-report.md` — marcar DB-08 a DB-23 como `✅`
- [x] `GEMINI.md` seção 8 — atualizar % por módulo
- [x] Criar PRs por módulo: `feat(m03): fecha DB-08 DB-09 DB-10`, `feat(m04): fecha DB-11 DB-12 DB-13 DB-14`, etc.

---

*SDD-002 criado pela Antigravity AI — 27/07/2026*
