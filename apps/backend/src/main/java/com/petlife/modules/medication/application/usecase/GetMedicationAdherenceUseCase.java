package com.petlife.modules.medication.application.usecase;

import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.medication.application.port.MedicationAdministrationRepositoryPort;
import com.petlife.modules.medication.domain.entity.MedicationAdministration;
import com.petlife.modules.medication.domain.entity.MedicationAdministrationStatus;
import com.petlife.modules.medication.infrastructure.dto.AdherenceResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetMedicationAdherenceUseCase {

    private final MedicationAdministrationRepositoryPort administrationRepository;
    private final PetRepositoryPort petRepository;

    @Transactional(readOnly = true)
    public AdherenceResponse execute(UUID petId, UUID userId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> BusinessException.notFound("PET_NOT_FOUND", "Pet não encontrado."));

        if (!pet.getUser().getId().equals(userId)) {
            throw BusinessException.forbidden("FORBIDDEN_PET_ACCESS", "Este pet não pertence ao usuário autenticado.");
        }

        List<MedicationAdministration> administrations = administrationRepository.findByMedicationPetId(petId);

        long totalDoses = administrations.size();
        long takenDoses = 0;
        long skippedDoses = 0;
        long lateDoses = 0;
        long pendingDoses = 0;
        long totalExpected = 0;

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        for (MedicationAdministration admin : administrations) {
            MedicationAdministrationStatus status = admin.getStatus();
            if (status == MedicationAdministrationStatus.TAKEN) {
                takenDoses++;
                totalExpected++;
            } else if (status == MedicationAdministrationStatus.SKIPPED) {
                skippedDoses++;
                totalExpected++;
            } else if (status == MedicationAdministrationStatus.LATE) {
                lateDoses++;
                totalExpected++;
            } else if (status == MedicationAdministrationStatus.PENDING) {
                pendingDoses++;
                if (admin.getScheduledTime().isBefore(now)) {
                    totalExpected++;
                }
            }
        }

        double adherenceRate = (totalExpected > 0) ? ((double) takenDoses / totalExpected) * 100.0 : 100.0;

        return new AdherenceResponse(
                adherenceRate,
                totalDoses,
                takenDoses,
                skippedDoses,
                lateDoses,
                pendingDoses
        );
    }
}
