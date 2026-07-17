package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.application.port.SaveWeightRecordPort;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.entity.WeightRecord;
import com.petlife.modules.pet.infrastructure.dto.PetResponse;
import com.petlife.modules.pet.infrastructure.dto.UpdatePetRequest;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdatePetUseCase {

    private final PetRepositoryPort petRepository;
    private final SaveWeightRecordPort saveWeightRecordPort;

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

        BigDecimal oldWeight = pet.getWeightKg();
        BigDecimal newWeight = request.weightKg();
        boolean weightChanged = false;
        
        if (newWeight != null) {
            if (oldWeight == null || oldWeight.compareTo(newWeight) != 0) {
                weightChanged = true;
            }
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

        if (weightChanged) {
            WeightRecord weightRecord = new WeightRecord();
            weightRecord.setPet(savedPet);
            weightRecord.setWeightKg(newWeight);
            weightRecord.setRecordedAt(OffsetDateTime.now());
            saveWeightRecordPort.save(weightRecord);
        }

        return PetResponse.fromEntity(savedPet);
    }
}
