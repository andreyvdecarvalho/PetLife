package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.RoutineActivityRepositoryPort;
import com.petlife.modules.pet.entity.RoutineActivity;
import com.petlife.modules.pet.entity.RoutineActivityStatus;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UpdateRoutineActivityStatusUseCase {

    private final RoutineActivityRepositoryPort routineActivityRepository;

    @Transactional
    public RoutineActivity execute(UUID id, RoutineActivityStatus status) {
        RoutineActivity activity = routineActivityRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("ROUTINE_ACTIVITY_NOT_FOUND", "Routine activity not found"));

        activity.setStatus(status);
        return routineActivityRepository.save(activity);
    }
}
