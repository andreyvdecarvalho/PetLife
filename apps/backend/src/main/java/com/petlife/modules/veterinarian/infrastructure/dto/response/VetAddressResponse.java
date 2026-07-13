package com.petlife.modules.veterinarian.infrastructure.dto.response;

import com.petlife.modules.veterinarian.entity.VetAddress;

import java.math.BigDecimal;
import java.util.UUID;

public record VetAddressResponse(
    UUID id,
    String label,
    String street,
    String number,
    String complement,
    String neighborhood,
    String city,
    String state,
    String postalCode,
    BigDecimal latitude,
    BigDecimal longitude,
    boolean isPrimary
) {
    public static VetAddressResponse fromEntity(VetAddress entity) {
        return new VetAddressResponse(
            entity.getId(),
            entity.getLabel(),
            entity.getStreet(),
            entity.getNumber(),
            entity.getComplement(),
            entity.getNeighborhood(),
            entity.getCity(),
            entity.getState(),
            entity.getPostalCode(),
            entity.getLatitude(),
            entity.getLongitude(),
            entity.isPrimary()
        );
    }
}
