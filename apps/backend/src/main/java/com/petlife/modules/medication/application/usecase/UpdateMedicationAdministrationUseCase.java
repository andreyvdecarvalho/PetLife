package com.petlife.modules.medication.application.usecase;

import com.petlife.modules.medication.application.port.MedicationAdministrationRepositoryPort;
import com.petlife.modules.medication.domain.entity.MedicationAdministration;
import com.petlife.modules.medication.domain.entity.MedicationAdministrationStatus;
import com.petlife.modules.medication.infrastructure.dto.MedicationAdministrationResponse;
import com.petlife.modules.medication.infrastructure.dto.UpdateAdministrationRequest;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UpdateMedicationAdministrationUseCase {

    private final MedicationAdministrationRepositoryPort administrationRepository;

    @Transactional
    public MedicationAdministrationResponse execute(UUID doseId, UUID userId, UpdateAdministrationRequest request) {
        MedicationAdministration administration = administrationRepository.findById(doseId)
                .orElseThrow(() -> BusinessException.notFound("DOSE_NOT_FOUND", "Dose não encontrada."));

        if (!administration.getMedication().getPetEntity().getUser().getId().equals(userId)) {
            throw BusinessException.forbidden("FORBIDDEN_PET_ACCESS", "Este pet não pertence ao usuário autenticado.");
        }

        administration.setStatus(request.status());
        if (request.status() == MedicationAdministrationStatus.TAKEN
                || request.status() == MedicationAdministrationStatus.LATE) {
            administration.setAdministeredAt(OffsetDateTime.now(ZoneOffset.UTC));
            administration.setSkippedReason(null);
        } else if (request.status() == MedicationAdministrationStatus.SKIPPED) {
            administration.setAdministeredAt(null);
            administration.setSkippedReason(request.skippedReason());
        } else {
            administration.setAdministeredAt(null);
            administration.setSkippedReason(null);
        }

        MedicationAdministration saved = administrationRepository.save(administration);

        return mapToResponse(saved);
    }

    private MedicationAdministrationResponse mapToResponse(MedicationAdministration admin) {
        return new MedicationAdministrationResponse(
                admin.getId(),
                admin.getMedication().getId(),
                admin.getMedication().getName(),
                admin.getScheduledTime(),
                admin.getAdministeredAt(),
                admin.getStatus(),
                admin.getSkippedReason(),
                admin.getCreatedAt(),
                admin.getUpdatedAt()
        );
    }
}
