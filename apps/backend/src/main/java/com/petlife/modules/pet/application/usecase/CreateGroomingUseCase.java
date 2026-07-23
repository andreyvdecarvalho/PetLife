package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.GroomingRepositoryPort;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.domain.entity.Grooming;
import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.modules.pet.infrastructure.dto.CreateGroomingRequest;
import com.petlife.modules.pet.infrastructure.dto.GroomingResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreateGroomingUseCase {

    private final GroomingRepositoryPort groomingRepositoryPort;
    private final PetRepositoryPort petRepositoryPort;

    @Transactional
    public GroomingResponse execute(UUID petId, UUID userId, CreateGroomingRequest request) {
        Pet pet = petRepositoryPort.findById(petId)
                .orElseThrow(() -> BusinessException.notFound("PET_NOT_FOUND", "Pet não encontrado."));

        if (!pet.getUser().getId().equals(userId)) {
            throw BusinessException.forbidden("FORBIDDEN_PET_ACCESS", "Este pet não pertence ao usuário autenticado.");
        }

        Grooming grooming = new Grooming();
        grooming.setPet(pet);
        grooming.setType(request.getType());
        grooming.setDate(request.getDate());
        grooming.setProvider(request.getProvider());
        grooming.setCost(request.getCost());
        grooming.setFrequencyDays(request.getFrequencyDays());
        grooming.setNotes(request.getNotes());
        grooming.setPhotos(new ArrayList<>());

        grooming.calculateNextDate();

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
