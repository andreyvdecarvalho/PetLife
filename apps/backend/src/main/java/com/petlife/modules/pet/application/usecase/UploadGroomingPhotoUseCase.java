package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.GroomingRepositoryPort;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Grooming;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.infrastructure.dto.GroomingResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UploadGroomingPhotoUseCase {

    private final GroomingRepositoryPort groomingRepositoryPort;
    private final PetRepositoryPort petRepositoryPort;

    @Transactional
    public GroomingResponse execute(UUID petId, UUID groomingId, UUID userId, MultipartFile file, String type) {
        if (file == null || file.isEmpty()) {
            throw BusinessException.badRequest("FILE_EMPTY", "File is empty or not provided");
        }

        Pet pet = petRepositoryPort.findById(petId)
                .orElseThrow(() -> BusinessException.notFound("PET_NOT_FOUND", "Pet não encontrado."));

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
            throw BusinessException.badRequest("INVALID_PHOTO_TYPE", "Photo type is required");
        }

        String typeStr = type.toLowerCase();
        if (!typeStr.equals("before") && !typeStr.equals("after")) {
            throw BusinessException.badRequest("INVALID_PHOTO_TYPE", "Photo type must be 'before' or 'after'");
        }

        List<String> photos = new ArrayList<>(grooming.getPhotos() != null ? grooming.getPhotos() : new ArrayList<>());

        String targetUrl = "/uploads/grooming-" + groomingId + "-" + typeStr + ".jpg";

        // Remove existing photo of the same type if present to avoid duplicates
        photos.removeIf(url -> url.contains("-" + typeStr + ".jpg"));

        if (photos.size() >= 2) {
            throw BusinessException.badRequest("PHOTO_LIMIT_EXCEEDED", "Grooming can have at most 2 photos");
        }

        photos.add(targetUrl);
        grooming.setPhotos(photos);

        Grooming saved = groomingRepositoryPort.save(grooming);
        return mapToResponse(saved);
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
