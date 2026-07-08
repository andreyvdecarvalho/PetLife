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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ListGroomingsByPetUseCase {

    private final GroomingRepositoryPort groomingRepositoryPort;
    private final PetRepositoryPort petRepositoryPort;

    @Transactional(readOnly = true)
    public List<GroomingResponse> execute(UUID petId, UUID userId) {
        Pet pet = petRepositoryPort.findById(petId)
                .orElseThrow(() -> BusinessException.notFound("PET_NOT_FOUND", "Pet não encontrado."));

        if (!pet.getUser().getId().equals(userId)) {
            throw BusinessException.forbidden("FORBIDDEN_PET_ACCESS", "Este pet não pertence ao usuário autenticado.");
        }

        List<Grooming> groomings = groomingRepositoryPort.findAllByPetId(petId);
        return groomings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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
