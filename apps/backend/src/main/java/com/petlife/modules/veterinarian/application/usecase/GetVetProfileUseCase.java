package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.veterinarian.application.port.VeterinarianRepositoryPort;
import com.petlife.modules.veterinarian.entity.Veterinarian;
import com.petlife.modules.veterinarian.infrastructure.dto.response.VeterinarianResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetVetProfileUseCase {

    private final VeterinarianRepositoryPort veterinarianRepository;

    public VeterinarianResponse execute(UUID veterinarianId) {
        Veterinarian vet = veterinarianRepository.findById(veterinarianId)
                .orElseThrow(() -> BusinessException.notFound("VET_NOT_FOUND", "Veterinário não encontrado."));

        return VeterinarianResponse.fromEntity(vet);
    }
}
