package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.veterinarian.application.port.GeocodingPort;
import com.petlife.modules.veterinarian.application.port.VetAddressRepositoryPort;
import com.petlife.modules.veterinarian.application.port.VeterinarianRepositoryPort;
import com.petlife.modules.veterinarian.entity.VetAddress;
import com.petlife.modules.veterinarian.entity.Veterinarian;
import com.petlife.modules.veterinarian.infrastructure.dto.request.UpdateVetAddressRequest;
import com.petlife.modules.veterinarian.infrastructure.dto.response.VetAddressResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateVetAddressUseCase {
    private final VeterinarianRepositoryPort veterinarianRepository;
    private final VetAddressRepositoryPort vetAddressRepository;
    private final GeocodingPort geocodingPort;

    @Transactional
    public VetAddressResponse execute(UUID userId, UUID addressId, UpdateVetAddressRequest request) {
        Veterinarian vet = veterinarianRepository.findByUserId(userId)
        .orElseThrow(() -> BusinessException.notFound(
                "VET_NOT_FOUND",
                "Perfil de veterinário não encontrado."));

        VetAddress address = vetAddressRepository.findByIdAndVeterinarianId(addressId, vet.getId())
                .orElseThrow(() -> BusinessException.notFound("ADDRESS_NOT_FOUND", "Endereço não encontrado."));

        String addressLine = String.format("%s, %s, %s, %s",
                request.street(), request.neighborhood(), request.city(), request.state());
        GeocodingPort.GeocodingResult geoResult = geocodingPort.geocode(request.postalCode(),
                request.number(), addressLine);

        address.setLabel(request.label());
        address.setStreet(request.street());
        address.setNumber(request.number());
        address.setComplement(request.complement());
        address.setNeighborhood(request.neighborhood());
        address.setCity(request.city());
        address.setState(request.state());
        address.setPostalCode(request.postalCode());
        address.setPrimary(request.isPrimary());

        if (geoResult != null) {
            address.setLatitude(geoResult.latitude());
            address.setLongitude(geoResult.longitude());
        }

        VetAddress saved = vetAddressRepository.save(address);
        return VetAddressResponse.fromEntity(saved);
    }
}
