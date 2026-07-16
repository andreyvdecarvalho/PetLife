package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.VaccinationPort;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Vaccination;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteVaccinationUseCase {

    private final VaccinationPort vaccinationPort;
    private final PetRepositoryPort petRepositoryPort;

    @Transactional
    public void execute(UUID petId, UUID vaccinationId, UUID userId) {
        Pet pet = petRepositoryPort.findById(petId)
                .orElseThrow(() -> BusinessException.notFound("PET_NOT_FOUND", "Pet não encontrado."));

        if (!pet.getUser().getId().equals(userId)) {
            throw BusinessException.forbidden("FORBIDDEN_PET_ACCESS", "Este pet não pertence ao usuário autenticado.");
        }

        Vaccination vaccination = vaccinationPort.findById(vaccinationId)
                .orElseThrow(() -> BusinessException.notFound("VACCINATION_NOT_FOUND", "Vacina não encontrada."));

        if (!vaccination.getPet().getId().equals(petId)) {
            throw BusinessException.badRequest("VACCINATION_PET_MISMATCH", "Esta vacina não pertence a este pet.");
        }

        vaccinationPort.delete(vaccination);
        log.info("Vacina {} excluída com sucesso para o pet {}", vaccinationId, petId);
    }
}
