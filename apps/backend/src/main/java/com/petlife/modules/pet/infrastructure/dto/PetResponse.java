package com.petlife.modules.pet.infrastructure.dto;

import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.modules.pet.domain.entity.PetSex;
import com.petlife.modules.pet.domain.entity.PetSize;
import com.petlife.modules.pet.domain.entity.PetSpecies;
import com.petlife.modules.pet.domain.entity.PetStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PetResponse(
    UUID id,
    UUID userId,
    String name,
    PetSpecies species,
    String breed,
    PetSex sex,
    LocalDate birthDate,
    BigDecimal weightKg,
    PetSize size,
    boolean neutered,
    String microchipId,
    String allergies,
    String notes,
    String photoUrl,
    PetStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static PetResponse fromEntity(Pet pet) {
        return new PetResponse(
            pet.getId(),
            pet.getUser().getId(),
            pet.getName(),
            pet.getSpecies(),
            pet.getBreed(),
            pet.getSex(),
            pet.getBirthDate(),
            pet.getWeightKg(),
            pet.getSize(),
            pet.isNeutered(),
            pet.getMicrochipId(),
            pet.getAllergies(),
            pet.getNotes(),
            pet.getPhotoUrl(),
            pet.getStatus(),
            pet.getCreatedAt(),
            pet.getUpdatedAt()
        );
    }
}
