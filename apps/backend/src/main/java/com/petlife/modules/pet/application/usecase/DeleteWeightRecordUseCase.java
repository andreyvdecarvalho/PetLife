package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.DeleteWeightRecordPort;
import com.petlife.modules.pet.application.port.FindWeightRecordPort;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.entity.WeightRecord;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteWeightRecordUseCase {

    private final FindWeightRecordPort findWeightRecordPort;
    private final DeleteWeightRecordPort deleteWeightRecordPort;
    private final PetRepositoryPort petRepository;

    @Transactional
    public void execute(UUID userId, UUID petId, UUID weightId) {
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

        deleteWeightRecordPort.delete(weightRecord);
        log.info("Registro de peso (ID: {}) deletado com sucesso para o pet {}", weightId, petId);
    }
}
