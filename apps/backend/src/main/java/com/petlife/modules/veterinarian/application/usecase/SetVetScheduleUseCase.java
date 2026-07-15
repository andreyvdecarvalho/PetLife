package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.veterinarian.application.port.VetScheduleRepositoryPort;
import com.petlife.modules.veterinarian.application.port.VeterinarianRepositoryPort;
import com.petlife.modules.veterinarian.entity.VetSchedule;
import com.petlife.modules.veterinarian.entity.Veterinarian;
import com.petlife.modules.veterinarian.infrastructure.dto.request.SetVetScheduleRequest;
import com.petlife.modules.veterinarian.infrastructure.dto.response.VetScheduleResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SetVetScheduleUseCase {

    private final VeterinarianRepositoryPort veterinarianRepository;
    private final VetScheduleRepositoryPort vetScheduleRepository;

    @Transactional
    public VetScheduleResponse execute(UUID userId, SetVetScheduleRequest request) {
        Veterinarian vet = veterinarianRepository.findByUserId(userId)
                .orElseThrow(() -> BusinessException.notFound("VET_NOT_FOUND",
                        "Perfil de veterinário não encontrado."));

        if (!request.closeTime().isAfter(request.openTime())) {
            throw BusinessException.badRequest("INVALID_TIME_RANGE",
                    "O horário de fechamento deve ser posterior ao horário de abertura.");
        }

        VetSchedule schedule = new VetSchedule();
        schedule.setVeterinarian(vet);
        schedule.setDayOfWeek(request.dayOfWeek());
        schedule.setOpenTime(request.openTime());
        schedule.setCloseTime(request.closeTime());
        schedule.setActive(request.isActive());

        VetSchedule saved = vetScheduleRepository.save(schedule);

        return VetScheduleResponse.fromEntity(saved);
    }
}
