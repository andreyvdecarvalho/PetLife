package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.FindWeightRecordPort;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.application.port.SaveWeightRecordPort;
import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.modules.pet.domain.entity.WeightRecord;
import com.petlife.modules.pet.infrastructure.dto.UpdateWeightRecordRequest;
import com.petlife.modules.pet.infrastructure.dto.WeightRecordResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateWeightRecordUseCase {

    private final FindWeightRecordPort findWeightRecordPort;
    private final SaveWeightRecordPort saveWeightRecordPort;
    private final PetRepositoryPort petRepository;

    @Transactional
    public WeightRecordResponse execute(UUID userId, UUID petId, UUID weightId, UpdateWeightRecordRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> BusinessException.notFound("PET_NOT_FOUND", "Pet não encontrado."));

        if (!pet.getUser().getId().equals(userId)) {
            throw BusinessException.forbidden("FORBIDDEN_PET_ACCESS", "Este pet não pertence ao usuário autenticado.");
        }

        WeightRecord weightRecord = findWeightRecordPort.findById(weightId)
                .orElseThrow(() -> BusinessException.notFound(
                        "WEIGHT_RECORD_NOT_FOUND", "Registro de peso não encontrado."));

        if (!weightRecord.getPet().getId().equals(petId)) {
            throw new BusinessException("WEIGHT_RECORD_BELONGS_TO_ANOTHER_PET",
                    "Este registro de peso não pertence ao pet informado.",
                    org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        weightRecord.setWeightKg(request.weightKg());
        weightRecord.setRecordedAt(request.recordedAt());

        WeightRecord savedRecord = saveWeightRecordPort.save(weightRecord);
        log.info("Registro de peso (ID: {}) atualizado com sucesso para o pet {}", weightId, petId);

        return WeightRecordResponse.fromEntity(savedRecord);
    }
}
