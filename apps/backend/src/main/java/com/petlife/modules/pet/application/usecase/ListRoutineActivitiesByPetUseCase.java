package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.application.port.RoutineActivityRepositoryPort;
import com.petlife.modules.pet.domain.entity.RoutineActivity;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ListRoutineActivitiesByPetUseCase {

    private final RoutineActivityRepositoryPort routineActivityRepository;
    private final PetRepositoryPort petRepository;

    @Transactional(readOnly = true)
    public List<RoutineActivity> execute(UUID petId, LocalDate date) {
        if (petRepository.findById(petId).isEmpty()) {
            throw BusinessException.notFound("PET_NOT_FOUND", "Pet not found");
        }
        
        if (date != null) {
            return routineActivityRepository.findByPetIdAndActivityDateOrderByActivityTimeAsc(petId, date);
        }
        return routineActivityRepository.findByPetIdOrderByActivityDateAscActivityTimeAsc(petId);
    }
}
