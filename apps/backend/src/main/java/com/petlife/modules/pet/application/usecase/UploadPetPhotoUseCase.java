package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.infrastructure.dto.PetResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UploadPetPhotoUseCase {

    private final PetRepositoryPort petRepository;

    @Transactional
    public PetResponse execute(UUID userId, UUID petId, MultipartFile file) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> BusinessException.notFound("PET_NOT_FOUND", "Pet não encontrado."));

        if (!pet.getUser().getId().equals(userId)) {
            throw BusinessException.forbidden("FORBIDDEN_PET_ACCESS", "Este pet não pertence ao usuário autenticado.");
        }

        if (file.isEmpty()) {
            throw BusinessException.badRequest("EMPTY_FILE", "O arquivo de foto não pode ser vazio.");
        }

        // Validação de tamanho no backend (máx 500KB)
        if (file.getSize() > 500 * 1024) {
            throw BusinessException.badRequest("FILE_TOO_LARGE", "A foto do pet deve ter no máximo 500KB.");
        }

        // Simula upload gerando URL fictícia de S3
        String fakePhotoUrl = "https://s3.amazonaws.com/petlife/pets/" + petId + "_" + System.currentTimeMillis() + ".jpg";
        pet.setPhotoUrl(fakePhotoUrl);

        Pet savedPet = petRepository.save(pet);
        log.info("Foto atualizada com sucesso para o pet {} (URL: {})", petId, fakePhotoUrl);

        return PetResponse.fromEntity(savedPet);
    }
}
