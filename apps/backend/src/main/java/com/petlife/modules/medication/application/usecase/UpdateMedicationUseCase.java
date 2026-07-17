package com.petlife.modules.medication.application.usecase;

import com.petlife.modules.medication.application.port.MedicationAdministrationRepositoryPort;
import com.petlife.modules.medication.application.port.MedicationRepositoryPort;
import com.petlife.modules.medication.domain.entity.Medication;
import com.petlife.modules.medication.domain.entity.MedicationAdministration;
import com.petlife.modules.medication.domain.entity.MedicationAdministrationStatus;
import com.petlife.modules.medication.domain.entity.MedicationFrequency;
import com.petlife.modules.medication.domain.entity.MedicationStatus;
import com.petlife.modules.medication.infrastructure.dto.MedicationAdministrationResponse;
import com.petlife.modules.medication.infrastructure.dto.MedicationResponse;
import com.petlife.modules.medication.infrastructure.dto.UpdateMedicationRequest;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Pet;
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
public class UpdateMedicationUseCase {

    private final MedicationRepositoryPort medicationRepository;
    private final MedicationAdministrationRepositoryPort administrationRepository;
    private final PetRepositoryPort petRepository;

    @Transactional
    public MedicationResponse execute(UUID petId, UUID medicationId, UUID userId, UpdateMedicationRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> BusinessException.notFound("PET_NOT_FOUND", "Pet não encontrado."));

        if (!pet.getUser().getId().equals(userId)) {
            throw BusinessException.forbidden("FORBIDDEN_PET_ACCESS", "Este pet não pertence ao usuário autenticado.");
        }

        Medication medication = medicationRepository.findById(medicationId)
                .orElseThrow(() -> BusinessException.notFound("MEDICATION_NOT_FOUND", "Medicação não encontrada."));

        if (!medication.getPet().getId().equals(petId)) {
            throw BusinessException.badRequest("MEDICATION_PET_MISMATCH", "Esta medicação não pertence a este pet.");
        }

        if (medication.getStatus() != MedicationStatus.ACTIVE) {
            throw BusinessException.badRequest(
                    "MEDICATION_NOT_ACTIVE",
                    "Apenas medicações ativas podem ser alteradas.");
        }

        boolean scheduleChanged = false;

        if (request.getName() != null && !request.getName().isBlank()) {
            medication.setName(request.getName());
        }
        if (request.getDosage() != null && !request.getDosage().isBlank()) {
            medication.setDosage(request.getDosage());
        }

        if (request.getMedicationType() != null && request.getMedicationType() != medication.getMedicationType()) {
            medication.setMedicationType(request.getMedicationType());
        }

        if (request.getFrequency() != null && request.getFrequency() != medication.getFrequency()) {
            medication.setFrequency(request.getFrequency());
            scheduleChanged = true;
        }

        if (request.getCustomFrequencyHours() != null 
                && !request.getCustomFrequencyHours().equals(medication.getCustomFrequencyHours())) {
            medication.setCustomFrequencyHours(request.getCustomFrequencyHours());
            scheduleChanged = true;
        }

        if (request.getEndDate() != null && !request.getEndDate().equals(medication.getEndDate())) {
            medication.setEndDate(request.getEndDate());
            scheduleChanged = true;
        }

        if (request.getTimesOfDay() != null && !request.getTimesOfDay().equals(medication.getTimesOfDay())) {
            medication.setTimesOfDay(request.getTimesOfDay());
            scheduleChanged = true;
        }

        if (medication.getFrequency() == MedicationFrequency.CUSTOM && medication.getCustomFrequencyHours() == null) {
            throw BusinessException.badRequest(
                    "INVALID_FREQUENCY", 
                    "Frequência personalizada requer definição de horas.");
        }

        Medication savedMedication = medicationRepository.save(medication);

        if (scheduleChanged) {
            // Cancel all pending administrations
            List<MedicationAdministration> currentAdmins = medication.getAdministrations();
            if (currentAdmins != null) {
                List<MedicationAdministration> pending = currentAdmins.stream()
                        .filter(a -> a.getStatus() == MedicationAdministrationStatus.PENDING)
                        .toList();
                administrationRepository.deleteAll(pending);
                currentAdmins.removeAll(pending);
            }

            // Generate new ones from now onwards
            String tz = pet.getUser().getTimezone() != null 
                    ? pet.getUser().getTimezone().getZoneId() 
                    : "America/Sao_Paulo";
            ZoneId zoneId = ZoneId.of(tz);
            List<MedicationAdministration> newAdmins = generateFutureDoses(savedMedication, zoneId);
            
            if (!newAdmins.isEmpty()) {
                administrationRepository.saveAll(newAdmins);
                if (currentAdmins != null) {
                    currentAdmins.addAll(newAdmins);
                } else {
                    savedMedication.setAdministrations(newAdmins);
                }
            }
        }

        return mapToResponse(savedMedication);
    }

    private List<MedicationAdministration> generateFutureDoses(Medication medication, ZoneId zoneId) {
        List<MedicationAdministration> list = new ArrayList<>();
        OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);

        LocalDate start = LocalDate.now(zoneId);
        // Start date of generation should be today or medication.startDate, whichever is later
        if (medication.getStartDate().isAfter(start)) {
            start = medication.getStartDate();
        }

        LocalDate end = medication.getEndDate();
        if (end == null) {
            end = start.plusDays(30);
        }

        if (start.isAfter(end)) {
            return list;
        }

        List<String> times = medication.getTimesOfDay();
        MedicationFrequency freq = medication.getFrequency();

        if (freq == MedicationFrequency.ONCE) {
            if (!times.isEmpty()) {
                LocalTime time = LocalTime.parse(times.get(0));
                OffsetDateTime scheduled = getUtcDateTime(medication.getStartDate(), time, zoneId);
                if (scheduled.isAfter(nowUtc) || scheduled.isEqual(nowUtc)) {
                    list.add(createAdministration(medication, scheduled));
                }
            }
        } else if (freq == MedicationFrequency.WEEKLY) {
            LocalDate current = start;
            while (!current.isAfter(end)) {
                // Ensure it's the same day of week as the original start date
                if (current.getDayOfWeek() == medication.getStartDate().getDayOfWeek()) {
                    for (String tStr : times) {
                        LocalTime time = LocalTime.parse(tStr);
                        OffsetDateTime scheduled = getUtcDateTime(current, time, zoneId);
                        if (scheduled.isAfter(nowUtc) || scheduled.isEqual(nowUtc)) {
                            list.add(createAdministration(medication, scheduled));
                        }
                    }
                }
                current = current.plusDays(1);
            }
        } else if (freq == MedicationFrequency.CUSTOM) {
            if (!times.isEmpty() && medication.getCustomFrequencyHours() != null) {
                int hours = medication.getCustomFrequencyHours();
                LocalTime time = LocalTime.parse(times.get(0));
                OffsetDateTime currentScheduled = getUtcDateTime(medication.getStartDate(), time, zoneId);
                
                // Limite do loop
                OffsetDateTime endLimit = getUtcDateTime(end, LocalTime.MAX, zoneId);

                // Fast forward to now
                while (currentScheduled.isBefore(nowUtc)) {
                    currentScheduled = currentScheduled.plusHours(hours);
                }

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
                med.getMedicationType(),
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
