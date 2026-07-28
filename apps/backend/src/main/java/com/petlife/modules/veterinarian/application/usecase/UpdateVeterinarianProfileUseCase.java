package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.veterinarian.application.port.VeterinarianRepositoryPort;
import com.petlife.modules.veterinarian.domain.entity.Veterinarian;
import com.petlife.modules.veterinarian.infrastructure.dto.request.UpdateVeterinarianRequest;
import com.petlife.modules.veterinarian.infrastructure.dto.response.VeterinarianResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateVeterinarianProfileUseCase {

    private final VeterinarianRepositoryPort veterinarianRepository;

    @Transactional
    public VeterinarianResponse execute(UUID userId, UpdateVeterinarianRequest request) {
        Veterinarian vet = veterinarianRepository.findByUserId(userId)
                .orElseThrow(() -> BusinessException.notFound(
                        "VET_NOT_FOUND", "Perfil de veterinário não encontrado."));

        if (request.getFullName() != null) {
            vet.setFullName(request.getFullName());
        }
        if (request.getBio() != null) {
            vet.setBio(request.getBio());
        }
        if (request.getSpecialties() != null) {
            vet.setSpecialties(request.getSpecialties());
        }
        if (request.getSpeciesServed() != null) {
            vet.setSpeciesServed(request.getSpeciesServed());
        }
        if (request.getModalities() != null) {
            vet.setModalities(request.getModalities());
        }
        if (request.getPaymentTypes() != null) {
            vet.setPaymentTypes(request.getPaymentTypes());
        }
        if (request.getInsurancePlans() != null) {
            vet.setInsurancePlans(request.getInsurancePlans());
        }
        if (request.getProfilePhotoUrl() != null) {
            vet.setProfilePhotoUrl(request.getProfilePhotoUrl());
        }
        if (request.getPhone() != null) {
            vet.setPhone(request.getPhone());
        }
        if (request.getWebsiteUrl() != null) {
            vet.setWebsiteUrl(request.getWebsiteUrl());
        }

        Veterinarian saved = veterinarianRepository.save(vet);
        return VeterinarianResponse.fromEntity(saved);
    }
}
