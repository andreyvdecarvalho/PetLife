package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.veterinarian.application.port.VetScheduleRepositoryPort;
import com.petlife.modules.veterinarian.application.port.VeterinarianRepositoryPort;
import com.petlife.modules.veterinarian.domain.entity.VetSchedule;
import com.petlife.modules.veterinarian.domain.entity.Veterinarian;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteVetScheduleUseCase {

    private final VeterinarianRepositoryPort veterinarianRepository;
    private final VetScheduleRepositoryPort vetScheduleRepository;

    @Transactional
    public void execute(UUID userId, UUID scheduleId) {
        Veterinarian vet = veterinarianRepository.findByUserId(userId)
                .orElseThrow(() -> BusinessException.notFound("VET_NOT_FOUND",
                        "Perfil de veterinário não encontrado."));

        VetSchedule schedule = vetScheduleRepository.findByIdAndVeterinarianId(scheduleId, vet.getId())
                .orElseThrow(() -> BusinessException.notFound("SCHEDULE_NOT_FOUND",
                        "Horário não encontrado."));

        vetScheduleRepository.delete(schedule);
    }
}
