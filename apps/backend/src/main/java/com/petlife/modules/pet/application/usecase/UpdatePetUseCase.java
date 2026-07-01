package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.infrastructure.dto.PetResponse;
import com.petlife.modules.pet.infrastructure.dto.UpdatePetRequest;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdatePetUseCase {

    private final PetRepositoryPort petRepository;

    @Transactional
    public PetResponse execute(UUID userId, UUID petId, UpdatePetRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> BusinessException.notFound("PET_NOT_FOUND", "Pet não encontrado."));

        if (!pet.getUser().getId().equals(userId)) {
            throw BusinessException.forbidden(
                    "FORBIDDEN_PET_ACCESS", 
                    "Este pet não pertence ao usuário autenticado."
            );
        }

        pet.setName(request.name());
        pet.setSpecies(request.species());
        pet.setBreed(request.breed());
        pet.setSex(request.sex());
        pet.setBirthDate(request.birthDate());
        pet.setWeightKg(request.weightKg());
        pet.setSize(request.size());
        pet.setNeutered(request.neutered());
        pet.setMicrochipId(request.microchipId());
        pet.setAllergies(request.allergies());
        pet.setNotes(request.notes());

        Pet savedPet = petRepository.save(pet);
        log.info("Pet atualizado com sucesso: {} (ID: {})", 
                savedPet.getName(), savedPet.getId());

        return PetResponse.fromEntity(savedPet);
    }
}
