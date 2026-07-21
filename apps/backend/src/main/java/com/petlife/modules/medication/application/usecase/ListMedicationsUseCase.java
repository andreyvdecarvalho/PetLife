package com.petlife.modules.medication.application.usecase;

import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.modules.medication.application.port.MedicationRepositoryPort;
import com.petlife.modules.medication.domain.entity.Medication;
import com.petlife.modules.medication.infrastructure.dto.MedicationResponse;
import com.petlife.modules.medication.infrastructure.dto.MedicationAdministrationResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ListMedicationsUseCase {

    private final MedicationRepositoryPort medicationRepository;
    private final PetRepositoryPort petRepository;

    @Transactional(readOnly = true)
    public List<MedicationResponse> execute(UUID petId, UUID userId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> BusinessException.notFound("PET_NOT_FOUND", "Pet não encontrado."));

        if (!pet.getUser().getId().equals(userId)) {
            throw BusinessException.forbidden("FORBIDDEN_PET_ACCESS", "Este pet não pertence ao usuário autenticado.");
        }

        List<Medication> medications = medicationRepository.findByPetEntityId(petId);

        return medications.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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
                med.getPetEntity().getId(),
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
