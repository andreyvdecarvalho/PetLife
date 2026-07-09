package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.medication.application.port.MedicationRepositoryPort;
import com.petlife.modules.medication.domain.entity.Medication;
import com.petlife.modules.pet.application.port.ConsultationRepositoryPort;
import com.petlife.modules.pet.application.port.GetPetWeightHistoryPort;
import com.petlife.modules.pet.application.port.GroomingRepositoryPort;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.application.port.VaccinationPort;
import com.petlife.modules.pet.entity.Consultation;
import com.petlife.modules.pet.entity.Grooming;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.entity.TimelineEventType;
import com.petlife.modules.pet.entity.Vaccination;
import com.petlife.modules.pet.entity.WeightRecord;
import com.petlife.modules.pet.infrastructure.dto.TimelineEventResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetPetTimelineUseCase {

    private final PetRepositoryPort petRepositoryPort;
    private final VaccinationPort vaccinationPort;
    private final ConsultationRepositoryPort consultationRepositoryPort;
    private final GroomingRepositoryPort groomingRepositoryPort;
    private final GetPetWeightHistoryPort getPetWeightHistoryPort;
    private final MedicationRepositoryPort medicationRepositoryPort;

    @Transactional(readOnly = true)
    public List<TimelineEventResponse> execute(
            UUID petId, UUID userId, List<TimelineEventType> filterTypes, int page, int size) {
        Pet pet = petRepositoryPort.findById(petId)
                .orElseThrow(() -> BusinessException.notFound("PET_NOT_FOUND", "Pet não encontrado."));

        if (!pet.getUser().getId().equals(userId)) {
            throw BusinessException.forbidden("FORBIDDEN_PET_ACCESS", "Este pet não pertence ao usuário autenticado.");
        }

        List<TimelineEventResponse> events = new ArrayList<>();

        // 1. Vaccination Events
        List<Vaccination> vaccinations = vaccinationPort.findByPetId(petId);
        for (Vaccination v : vaccinations) {
            events.add(TimelineEventResponse.builder()
                    .id(v.getId())
                    .type(TimelineEventType.VACCINE)
                    .date(toOffsetDateTime(v.getDateAdministered()))
                    .title("Vacina: " + v.getVaccineName())
                    .description("Aplicada por: "
                            + (v.getVeterinarian() != null ? v.getVeterinarian() : "Não informado") + 
                                 (v.getClinic() != null ? " (Clínica: " + v.getClinic() + ")" : ""))
                    .icon("vaccines")
                    .color("#006b55")
                    .photoUrl(v.getProofUrl())
                    .build());
        }

        // 2. Consultation Events
        List<Consultation> consultations = consultationRepositoryPort.findAllByPetId(petId);
        for (Consultation c : consultations) {
            events.add(TimelineEventResponse.builder()
                    .id(c.getId())
                    .type(TimelineEventType.CONSULTATION)
                    .date(c.getDate())
                    .title("Consulta: " + c.getReason())
                    .description("Diagnóstico: " + (c.getDiagnosis() != null ? c.getDiagnosis() : "Não informado"))
                    .icon("medical_services")
                    .color("#005fac")
                    .photoUrl(c.getAttachments() != null && !c.getAttachments().isEmpty()
                            ? c.getAttachments().get(0) : null)
                    .build());
        }

        // 3. Grooming Events
        List<Grooming> groomings = groomingRepositoryPort.findAllByPetId(petId);
        for (Grooming g : groomings) {
            String typeStr = g.getType() != null ? g.getType().name() : "ESTÉTICA";
            if ("BATH".equals(typeStr)) {
                typeStr = "Banho";
            } else if ("GROOMING".equals(typeStr)) {
                typeStr = "Tosa";
            } else if ("BATH_AND_GROOMING".equals(typeStr)) {
                typeStr = "Banho e Tosa";
            }

            events.add(TimelineEventResponse.builder()
                    .id(g.getId())
                    .type(TimelineEventType.GROOMING)
                    .date(toOffsetDateTime(g.getDate()))
                    .title("Serviço de Estética: " + typeStr)
                    .description("Local: " + (g.getProvider() != null ? g.getProvider() : "Não informado") + 
                                 (g.getNotes() != null ? " - " + g.getNotes() : ""))
                    .icon("content_cut")
                    .color("#EC4899")
                    .photoUrl(g.getPhotos() != null && !g.getPhotos().isEmpty() ? g.getPhotos().get(0) : null)
                    .build());
        }

        // 4. Weight Record Events
        List<WeightRecord> weights = getPetWeightHistoryPort.getWeightHistory(petId);
        for (WeightRecord w : weights) {
            events.add(TimelineEventResponse.builder()
                    .id(w.getId())
                    .type(TimelineEventType.WEIGHT)
                    .date(w.getRecordedAt())
                    .title("Registro de Peso: " + w.getWeightKg() + " kg")
                    .description("Acompanhamento de peso.")
                    .icon("scale")
                    .color("#14B8A6")
                    .build());
        }

        // 5. Medication Events (Start and End)
        List<Medication> medications = medicationRepositoryPort.findByPetId(petId);
        for (Medication m : medications) {
            // Start Event
            events.add(TimelineEventResponse.builder()
                    .id(m.getId())
                    .type(TimelineEventType.MEDICATION_START)
                    .date(toOffsetDateTime(m.getStartDate()))
                    .title("Início de Medicamento: " + m.getName())
                    .description("Dosagem: " + m.getDosage() + " | Frequência: " + m.getFrequency())
                    .icon("medication")
                    .color("#8B5CF6")
                    .build());

            // End Event (if exists)
            if (m.getEndDate() != null) {
                events.add(TimelineEventResponse.builder()
                        .id(m.getId())
                        .type(TimelineEventType.MEDICATION_END)
                        .date(toOffsetDateTime(m.getEndDate()))
                        .title("Fim de Medicamento: " + m.getName())
                        .description("Tratamento concluído.")
                        .icon("check_circle")
                        .color("#6B7280")
                        .build());
            }
        }

        // 6. Birthday Events
        if (pet.getBirthDate() != null) {
            LocalDate birth = pet.getBirthDate();
            LocalDate now = LocalDate.now();
            int currentYear = now.getYear();
            for (int year = birth.getYear() + 1; year <= currentYear; year++) {
                LocalDate birthdayDate = birth.plusYears(year - birth.getYear());
                if (!birthdayDate.isAfter(now)) {
                    int age = year - birth.getYear();
                    events.add(TimelineEventResponse.builder()
                            .id(null)
                            .type(TimelineEventType.BIRTHDAY)
                            .date(toOffsetDateTime(birthdayDate))
                            .title("Aniversário de " + age + " " + (age == 1 ? "ano" : "anos")
                                    + " de " + pet.getName() + "!")
                            .description("Parabéns, " + pet.getName() + "! Hoje completa "
                                    + age + " " + (age == 1 ? "ano" : "anos") + " de vida.")
                            .icon("cake")
                            .color("#EAB308")
                            .build());
                }
            }
        }

        // Apply filters if types list is provided and not empty
        if (filterTypes != null && !filterTypes.isEmpty()) {
            events = events.stream()
                    .filter(e -> filterTypes.contains(e.getType()))
                    .collect(Collectors.toList());
        }

        // Sort descending by date
        events.sort(Comparator.comparing(e -> e.getDate(), Comparator.nullsLast(Comparator.reverseOrder())));

        // Paginate manually
        int start = page * size;
        if (start >= events.size()) {
            return Collections.emptyList();
        }
        int end = Math.min(start + size, events.size());
        return events.subList(start, end);
    }

    private OffsetDateTime toOffsetDateTime(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.atStartOfDay().atOffset(ZoneOffset.UTC);
    }
}
