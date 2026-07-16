package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.GroomingRepositoryPort;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Grooming;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.infrastructure.dto.GroomingResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UploadGroomingPhotoUseCase {

    private final GroomingRepositoryPort groomingRepositoryPort;
    private final PetRepositoryPort petRepositoryPort;

    /**
     * Realiza o upload da foto antes/depois do banho e tosa.
     * Em dev, armazena como Base64 Data URL. Em produção, usar AWS S3/MinIO.
     */
    @Transactional
    public GroomingResponse execute(UUID petId, UUID groomingId, UUID userId,
            MultipartFile file, String type) {
        if (file == null || file.isEmpty()) {
            throw BusinessException.badRequest("FILE_EMPTY",
                    "File is empty or not provided");
        }

        Pet pet = petRepositoryPort.findById(petId)
                .orElseThrow(() -> BusinessException.notFound("PET_NOT_FOUND",
                        "Pet não encontrado."));

        if (!pet.getUser().getId().equals(userId)) {
            throw BusinessException.forbidden("FORBIDDEN_PET_ACCESS",
                    "Este pet não pertence ao usuário autenticado.");
        }

        Grooming grooming = groomingRepositoryPort.findById(groomingId)
                .orElseThrow(() -> BusinessException.notFound("GROOMING_NOT_FOUND",
                        "Registro de banho e tosa não encontrado."));

        if (!grooming.getPet().getId().equals(petId)) {
            throw BusinessException.badRequest("GROOMING_PET_MISMATCH",
                    "Este registro de banho e tosa não pertence a este pet.");
        }

        if (type == null) {
            throw BusinessException.badRequest("INVALID_PHOTO_TYPE",
                    "Photo type is required");
        }

        String typeStr = type.toLowerCase();
        if (!typeStr.equals("before") && !typeStr.equals("after")) {
            throw BusinessException.badRequest("INVALID_PHOTO_TYPE",
                    "Photo type must be 'before' or 'after'");
        }

        List<String> photos = new ArrayList<>(
                grooming.getPhotos() != null ? grooming.getPhotos()
                        : new ArrayList<>());

        try {
            byte[] bytes = file.getBytes();
            String mimeType = file.getContentType() != null ? file.getContentType() : "image/jpeg";
            String base64 = Base64.getEncoder().encodeToString(bytes);
            String dataUrl = "data:" + mimeType + ";base64," + base64;

            // Remove existing photo of the same type (identified by position: before=index 0, after=index 1)
            if (typeStr.equals("before")) {
                if (photos.isEmpty()) {
                    photos.add(dataUrl);
                } else {
                    photos.set(0, dataUrl);
                }
            } else {
                if (photos.size() < 1) {
                    photos.add("");
                }
                if (photos.size() < 2) {
                    photos.add(dataUrl);
                } else {
                    photos.set(1, dataUrl);
                }
            }

            grooming.setPhotos(photos);
            Grooming saved = groomingRepositoryPort.save(grooming);
            log.info("Foto {} do grooming {} atualizada ({} bytes)", typeStr, groomingId, bytes.length);
            return mapToResponse(saved);
        } catch (IOException e) {
            log.error("Falha ao processar foto do grooming {}: {}", groomingId, e.getMessage());
            throw BusinessException.badRequest("FILE_UPLOAD_FAILED", "Falha ao processar a foto.");
        }
    }

    private GroomingResponse mapToResponse(Grooming grooming) {
        GroomingResponse response = new GroomingResponse();
        response.setId(grooming.getId());
        response.setPetId(grooming.getPet().getId());
        response.setType(grooming.getType());
        response.setDate(grooming.getDate());
        response.setProvider(grooming.getProvider());
        response.setCost(grooming.getCost());
        response.setFrequencyDays(grooming.getFrequencyDays());
        response.setNextDate(grooming.getNextDate());
        response.setNotes(grooming.getNotes());
        response.setPhotos(grooming.getPhotos());
        response.setCreatedAt(grooming.getCreatedAt());
        response.setUpdatedAt(grooming.getUpdatedAt());
        return response;
    }
}
