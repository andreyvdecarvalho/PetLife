package com.petlife.modules.medication.application.usecase;

import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.medication.application.port.MedicationRepositoryPort;
import com.petlife.modules.medication.application.port.MedicationAdministrationRepositoryPort;
import com.petlife.modules.medication.domain.entity.Medication;
import com.petlife.modules.medication.domain.entity.MedicationAdministration;
import com.petlife.modules.medication.domain.entity.MedicationAdministrationStatus;
import com.petlife.modules.medication.domain.entity.MedicationFrequency;
import com.petlife.modules.medication.domain.entity.MedicationStatus;
import com.petlife.modules.medication.infrastructure.dto.CreateMedicationRequest;
import com.petlife.modules.medication.infrastructure.dto.MedicationResponse;
import com.petlife.modules.medication.infrastructure.dto.MedicationAdministrationResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateMedicationUseCase {

    private final MedicationRepositoryPort medicationRepository;
    private final MedicationAdministrationRepositoryPort administrationRepository;
    private final PetRepositoryPort petRepository;

    @Transactional
    public MedicationResponse execute(UUID petId, UUID userId, CreateMedicationRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> BusinessException.notFound("PET_NOT_FOUND", "Pet não encontrado."));

        if (!pet.getUser().getId().equals(userId)) {
            throw BusinessException.forbidden("FORBIDDEN_PET_ACCESS", "Este pet não pertence ao usuário autenticado.");
        }

        if (request.frequency() == MedicationFrequency.CUSTOM && request.customFrequencyHours() == null) {
            throw BusinessException.badRequest("INVALID_FREQUENCY",
                    "Frequência personalizada requer definição de horas.");
        }

        Medication medication = new Medication();
        medication.setPet(pet);
        medication.setName(request.name());
        medication.setDosage(request.dosage());
        medication.setFrequency(request.frequency());
        medication.setCustomFrequencyHours(request.customFrequencyHours());
        medication.setStartDate(request.startDate());
        medication.setEndDate(request.endDate());
        medication.setTimesOfDay(request.timesOfDay());
        medication.setStatus(MedicationStatus.ACTIVE);

        Medication savedMedication = medicationRepository.save(medication);

        // Generate doses
        String tz = pet.getUser().getTimezone() != null ? pet.getUser().getTimezone().getZoneId() : "America/Sao_Paulo";
        ZoneId zoneId = ZoneId.of(tz);
        List<MedicationAdministration> administrations = generateDoses(savedMedication, zoneId);
        
        if (!administrations.isEmpty()) {
            administrationRepository.saveAll(administrations);
            savedMedication.setAdministrations(administrations);
        }

        log.info("Medicação cadastrada com sucesso: {} para o pet {} com {} doses geradas.",
                savedMedication.getName(), petId, administrations.size());

        return mapToResponse(savedMedication);
    }

    private List<MedicationAdministration> generateDoses(Medication medication, ZoneId zoneId) {
        List<MedicationAdministration> list = new ArrayList<>();
        OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);

        LocalDate start = medication.getStartDate();
        LocalDate end = medication.getEndDate();
        if (end == null) {
            // Se contínuo, projetamos para 30 dias a partir de hoje
            LocalDate nowLocal = LocalDate.now(zoneId);
            end = nowLocal.isAfter(start) ? nowLocal.plusDays(30) : start.plusDays(30);
        }

        List<String> times = medication.getTimesOfDay();
        MedicationFrequency freq = medication.getFrequency();

        if (freq == MedicationFrequency.ONCE) {
            if (!times.isEmpty()) {
                LocalTime time = LocalTime.parse(times.get(0));
                OffsetDateTime scheduled = getUtcDateTime(start, time, zoneId);
                if (scheduled.isAfter(nowUtc) || scheduled.isEqual(nowUtc)) {
                    list.add(createAdministration(medication, scheduled));
                }
            }
        } else if (freq == MedicationFrequency.WEEKLY) {
            LocalDate current = start;
            while (!current.isAfter(end)) {
                for (String tStr : times) {
                    LocalTime time = LocalTime.parse(tStr);
                    OffsetDateTime scheduled = getUtcDateTime(current, time, zoneId);
                    if (scheduled.isAfter(nowUtc) || scheduled.isEqual(nowUtc)) {
                        list.add(createAdministration(medication, scheduled));
                    }
                }
                current = current.plusWeeks(1);
            }
        } else if (freq == MedicationFrequency.CUSTOM) {
            if (!times.isEmpty() && medication.getCustomFrequencyHours() != null) {
                int hours = medication.getCustomFrequencyHours();
                LocalTime time = LocalTime.parse(times.get(0));
                OffsetDateTime currentScheduled = getUtcDateTime(start, time, zoneId);
                
                // Limite do loop
                OffsetDateTime endLimit = getUtcDateTime(end, LocalTime.MAX, zoneId);

                while (!currentScheduled.isAfter(endLimit)) {
                    if (currentScheduled.isAfter(nowUtc) || currentScheduled.isEqual(nowUtc)) {
                        list.add(createAdministration(medication, currentScheduled));
                    }
                    currentScheduled = currentScheduled.plusHours(hours);
                }
            }
        } else {
            // DAILY, TWICE_DAILY, EVERY_8H, EVERY_12H
            LocalDate current = start;
            while (!current.isAfter(end)) {
                for (String tStr : times) {
                    LocalTime time = LocalTime.parse(tStr);
                    OffsetDateTime scheduled = getUtcDateTime(current, time, zoneId);
                    if (scheduled.isAfter(nowUtc) || scheduled.isEqual(nowUtc)) {
                        list.add(createAdministration(medication, scheduled));
                    }
                }
                current = current.plusDays(1);
            }
        }

        return list;
    }

    private OffsetDateTime getUtcDateTime(LocalDate date, LocalTime time, ZoneId zoneId) {
        LocalDateTime localDateTime = LocalDateTime.of(date, time);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);
        return zonedDateTime.withZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime();
    }

    private MedicationAdministration createAdministration(Medication medication, OffsetDateTime scheduledTime) {
        MedicationAdministration admin = new MedicationAdministration();
        admin.setMedication(medication);
        admin.setScheduledTime(scheduledTime);
        admin.setStatus(MedicationAdministrationStatus.PENDING);
        return admin;
    }

    private MedicationResponse mapToResponse(Medication med) {
        List<MedicationAdministrationResponse> adminResponses = med.getAdministrations() == null ? List.of() :
            med.getAdministrations().stream().map(admin -> new MedicationAdministrationResponse(
                admin.getId(),
                admin.getMedication().getId(),
                admin.getMedication().getName(),
                admin.getScheduledTime(),
                admin.getAdministeredAt(),
                admin.getStatus(),
                admin.getSkippedReason(),
                admin.getCreatedAt(),
                admin.getUpdatedAt()
            )).toList();

        return new MedicationResponse(
                med.getId(),
                med.getPet().getId(),
                med.getName(),
                med.getDosage(),
                med.getFrequency(),
                med.getCustomFrequencyHours(),
                med.getStartDate(),
                med.getEndDate(),
                med.getTimesOfDay(),
                med.getStatus(),
                adminResponses,
                med.getCreatedAt(),
                med.getUpdatedAt()
        );
    }
}
