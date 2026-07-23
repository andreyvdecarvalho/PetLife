package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.GroomingRepositoryPort;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.domain.entity.Grooming;
import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteGroomingUseCase {

    private final GroomingRepositoryPort groomingRepositoryPort;
    private final PetRepositoryPort petRepositoryPort;

    @Transactional
    public void execute(UUID petId, UUID groomingId, UUID userId) {
        Pet pet = petRepositoryPort.findById(petId)
                .orElseThrow(() -> BusinessException.notFound("PET_NOT_FOUND", "Pet não encontrado."));

        if (!pet.getUser().getId().equals(userId)) {
            throw BusinessException.forbidden("FORBIDDEN_PET_ACCESS", "Este pet não pertence ao usuário autenticado.");
        }

        Grooming grooming = groomingRepositoryPort.findById(groomingId)
                .orElseThrow(() -> BusinessException.notFound(
                        "GROOMING_NOT_FOUND", "Registro de banho e tosa não encontrado."));

        if (!grooming.getPet().getId().equals(petId)) {
            throw BusinessException.badRequest("GROOMING_PET_MISMATCH", "Este registro não pertence a este pet.");
        }

        groomingRepositoryPort.delete(grooming);
        log.info("Registro de banho e tosa {} excluído com sucesso para o pet {}", groomingId, petId);
    }
}
