package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.modules.pet.infrastructure.dto.PetResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UploadPetPhotoUseCase {

    private final PetRepositoryPort petRepository;

    /**
     * Realiza o upload da foto do pet.
     * Em ambiente de desenvolvimento, a imagem é armazenada como Data URL (Base64).
     * Em produção, deverá ser substituído por integração com AWS S3 ou MinIO.
     */
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

        // Validação de tamanho: máx 2MB para Base64 (já que Base64 infla ~33%)
        if (file.getSize() > 2 * 1024 * 1024) {
            throw BusinessException.badRequest("FILE_TOO_LARGE", "A foto do pet deve ter no máximo 2MB.");
        }

        try {
            byte[] bytes = file.getBytes();
            String mimeType = file.getContentType() != null ? file.getContentType() : "image/jpeg";
            String base64 = Base64.getEncoder().encodeToString(bytes);
            String dataUrl = "data:" + mimeType + ";base64," + base64;

            pet.setPhotoUrl(dataUrl);
            Pet savedPet = petRepository.save(pet);
            log.info("Foto atualizada com sucesso para o pet {} (tamanho: {} bytes)", petId, bytes.length);
            return PetResponse.fromEntity(savedPet);
        } catch (IOException e) {
            log.error("Falha ao processar foto do pet {}: {}", petId, e.getMessage());
            throw BusinessException.badRequest("FILE_UPLOAD_FAILED", "Falha ao processar a foto do pet.");
        }
    }
}
