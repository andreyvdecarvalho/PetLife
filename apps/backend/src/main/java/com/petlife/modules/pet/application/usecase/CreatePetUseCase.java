package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.entity.User;
import com.petlife.modules.auth.entity.UserPlan;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.entity.PetStatus;
import com.petlife.modules.pet.infrastructure.dto.CreatePetRequest;
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
public class CreatePetUseCase {

    private final PetRepositoryPort petRepository;
    private final UserRepositoryPort userRepository;

    @Transactional
    public PetResponse execute(UUID userId, CreatePetRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("USER_NOT_FOUND", "Usuário não encontrado."));

        // Validar limites do plano Free (máx 2 pets)
        if (user.getPlan() == UserPlan.FREE) {
            long activePetsCount = petRepository.countByUserIdAndStatusNot(userId, PetStatus.ARCHIVED);
            if (activePetsCount >= 2) {
                throw new BusinessException("PET_LIMIT_REACHED", "O limite de pets para o plano Free foi atingido (máx: 2).", org.springframework.http.HttpStatus.BAD_REQUEST);
            }
        }

        Pet pet = new Pet();
        pet.setUser(user);
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
        pet.setStatus(PetStatus.ACTIVE);

        Pet savedPet = petRepository.save(pet);
        log.info("Pet cadastrado com sucesso: {} (ID: {}) para o usuário {}", savedPet.getName(), savedPet.getId(), userId);

        return PetResponse.fromEntity(savedPet);
    }
}
