package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.veterinarian.application.port.VeterinarianRepositoryPort;
import com.petlife.modules.veterinarian.domain.entity.Veterinarian;
import com.petlife.modules.veterinarian.infrastructure.dto.response.VeterinarianResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetMyVetProfileUseCase {
    private final VeterinarianRepositoryPort veterinarianRepository;

    public VeterinarianResponse execute(UUID userId) {
        Veterinarian vet = veterinarianRepository.findByUserId(userId)
        .orElseThrow(() -> BusinessException.notFound(
                "VET_NOT_FOUND",
                "Perfil de veterinário não encontrado."));
        return VeterinarianResponse.fromEntity(vet);
    }
}
