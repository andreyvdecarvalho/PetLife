package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.veterinarian.application.port.VeterinarianRepositoryPort;
import com.petlife.modules.veterinarian.entity.Veterinarian;
import com.petlife.modules.veterinarian.infrastructure.dto.request.UpdateAvailabilityRequest;
import com.petlife.modules.veterinarian.infrastructure.dto.response.VeterinarianResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateAvailabilityUseCase {

    private final VeterinarianRepositoryPort veterinarianRepository;

    @Transactional
    public VeterinarianResponse execute(UUID userId, UpdateAvailabilityRequest request) {
        Veterinarian vet = veterinarianRepository.findByUserId(userId)
                .orElseThrow(() -> BusinessException.notFound("VET_NOT_FOUND",
                        "Perfil de veterinário não encontrado."));

        vet.setAvailabilityStatus(request.availabilityStatus());
        vet.setEmergencyOnDuty(request.emergencyOnDuty());

        Veterinarian saved = veterinarianRepository.save(vet);
        return VeterinarianResponse.fromEntity(saved);
    }
}
