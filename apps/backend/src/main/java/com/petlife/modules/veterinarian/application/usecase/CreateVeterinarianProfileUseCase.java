package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.entity.User;
import com.petlife.modules.veterinarian.application.port.VeterinarianRepositoryPort;
import com.petlife.modules.veterinarian.domain.exception.CrmvAlreadyExistsException;
import com.petlife.modules.veterinarian.entity.Veterinarian;
import com.petlife.modules.veterinarian.infrastructure.dto.request.CreateVeterinarianRequest;
import com.petlife.modules.veterinarian.infrastructure.dto.response.VeterinarianResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CreateVeterinarianProfileUseCase {

    private final VeterinarianRepositoryPort veterinarianRepository;
    private final UserRepositoryPort userRepository;

    @Transactional
    public VeterinarianResponse execute(UUID userId, CreateVeterinarianRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("USER_NOT_FOUND", "User not found"));

        if (veterinarianRepository.existsByCrmvNumber(request.getCrmvNumber())) {
            throw new CrmvAlreadyExistsException("CRMV number already registered");
        }

        Veterinarian vet = new Veterinarian();
        vet.setUser(user);
        vet.setCrmvNumber(request.getCrmvNumber());
        vet.setCrmvState(request.getCrmvState());
        vet.setFullName(request.getFullName());
        vet.setBio(request.getBio());
        vet.setSpecialties(request.getSpecialties() != null ? request.getSpecialties() : java.util.List.of());
        vet.setSpeciesServed(request.getSpeciesServed() != null ? request.getSpeciesServed() : java.util.List.of());
        vet.setModalities(request.getModalities() != null ? request.getModalities() : java.util.List.of());
        vet.setPaymentTypes(request.getPaymentTypes() != null ? request.getPaymentTypes() : java.util.List.of());
        vet.setInsurancePlans(request.getInsurancePlans() != null ? request.getInsurancePlans() : java.util.List.of());
        vet.setProfilePhotoUrl(request.getProfilePhotoUrl());
        vet.setPhone(request.getPhone());
        vet.setWebsiteUrl(request.getWebsiteUrl());

        Veterinarian saved = veterinarianRepository.save(vet);
        return VeterinarianResponse.fromEntity(saved);
    }
}
