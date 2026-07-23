package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.application.port.RoutineActivityRepositoryPort;
import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.modules.pet.domain.entity.RoutineActivity;
import com.petlife.modules.pet.infrastructure.dto.CreateRoutineActivityRequest;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreateRoutineActivityUseCase {

    private final RoutineActivityRepositoryPort routineActivityRepository;
    private final PetRepositoryPort petRepository;

    @Transactional
    public RoutineActivity execute(UUID petId, CreateRoutineActivityRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> BusinessException.notFound("PET_NOT_FOUND", "Pet not found"));

        RoutineActivity activity = new RoutineActivity();
        activity.setPet(pet);
        activity.setTitle(request.title());
        activity.setDescription(request.description());
        activity.setActivityDate(request.activityDate());
        activity.setActivityTime(request.activityTime());
        activity.setType(request.type());
        activity.setStatus(request.status());

        return routineActivityRepository.save(activity);
    }
}
