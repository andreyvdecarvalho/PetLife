package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.entity.PetStatus;
import com.petlife.modules.pet.infrastructure.dto.PetResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdatePetStatusUseCase {

    private final PetRepositoryPort petRepository;

    @Transactional
    public PetResponse execute(UUID userId, UUID petId, PetStatus newStatus) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> BusinessException.notFound("PET_NOT_FOUND", "Pet não encontrado."));

        if (!pet.getUser().getId().equals(userId)) {
            throw BusinessException.forbidden(
                    "FORBIDDEN_PET_ACCESS", 
                    "Este pet não pertence ao usuário autenticado."
            );
        }

        pet.setStatus(newStatus);
        Pet savedPet = petRepository.save(pet);
        log.info("Status do pet {} alterado para: {} (ID: {})", 
                savedPet.getName(), savedPet.getStatus(), savedPet.getId());

        return PetResponse.fromEntity(savedPet);
    }
}
