package com.petlife.modules.medication.application.usecase;

import com.petlife.modules.medication.application.port.MedicationRepositoryPort;
import com.petlife.modules.medication.application.port.MedicationAdministrationRepositoryPort;
import com.petlife.modules.medication.domain.entity.Medication;
import com.petlife.modules.medication.domain.entity.MedicationAdministration;
import com.petlife.modules.medication.domain.entity.MedicationAdministrationStatus;
import com.petlife.modules.medication.domain.entity.MedicationStatus;
import com.petlife.modules.medication.infrastructure.dto.MedicationResponse;
import com.petlife.modules.medication.infrastructure.dto.MedicationAdministrationResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StopMedicationUseCase {

    private final MedicationRepositoryPort medicationRepository;
    private final MedicationAdministrationRepositoryPort administrationRepository;

    @Transactional
    public MedicationResponse execute(UUID id, UUID userId) {
        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("MEDICATION_NOT_FOUND", "Medicamento não encontrado."));

        if (!medication.getPetOwnerId().equals(userId)) {
            throw BusinessException.forbidden("FORBIDDEN_PET_ACCESS", "Este pet não pertence ao usuário autenticado.");
        }

        medication.setStatus(MedicationStatus.CANCELLED);
        Medication saved = medicationRepository.save(medication);

        // Delete future pending doses
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        List<MedicationAdministration> futurePending = administrationRepository
                .findByMedicationIdAndStatusAndScheduledTimeAfter(id, MedicationAdministrationStatus.PENDING, now);

        if (!futurePending.isEmpty()) {
            administrationRepository.deleteAll(futurePending);
            log.info("Canceladas {} doses futuras pendentes do medicamento ID {}", futurePending.size(), id);
        }

        return mapToResponse(saved);
    }

    private MedicationResponse mapToResponse(Medication med) {
        List<MedicationAdministrationResponse> adminResponses = med.getAdministrations() == null ? List.of() :
            med.getAdministrations().stream().map(admin -> new MedicationAdministrationResponse(
                admin.getId(),
                admin.getMedicationId(),
                admin.getMedicationName(),
                admin.getScheduledTime(),
                admin.getAdministeredAt(),
                admin.getStatus(),
                admin.getSkippedReason(),
                admin.getCreatedAt(),
                admin.getUpdatedAt()
            )).toList();

        return new MedicationResponse(
                med.getId(),
                med.getPetId(),
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
