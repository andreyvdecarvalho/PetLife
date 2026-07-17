package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.veterinarian.application.port.VetAddressRepositoryPort;
import com.petlife.modules.veterinarian.application.port.VeterinarianRepositoryPort;
import com.petlife.modules.veterinarian.entity.VetAddress;
import com.petlife.modules.veterinarian.entity.Veterinarian;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteVetAddressUseCase {
    private final VeterinarianRepositoryPort veterinarianRepository;
    private final VetAddressRepositoryPort vetAddressRepository;

    @Transactional
    public void execute(UUID userId, UUID addressId) {
        Veterinarian vet = veterinarianRepository.findByUserId(userId)
                .orElseThrow(() -> BusinessException.notFound(
                        "VET_NOT_FOUND",
                        "Perfil de veterinário não encontrado."));

        VetAddress address = vetAddressRepository.findByIdAndVeterinarianId(addressId, vet.getId())
                .orElseThrow(() -> BusinessException.notFound(
                        "ADDRESS_NOT_FOUND",
                        "Endereço não encontrado."));

        vetAddressRepository.delete(address);
    }
}
