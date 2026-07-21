package com.petlife.modules.pet.domain.entity;

import com.petlife.modules.auth.domain.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Pet {

    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User user;
    private String name;
    private PetSpecies species;
    private String breed;
    private PetSex sex;
    private LocalDate birthDate;
    private BigDecimal weightKg;
    private PetSize size;
    private boolean neutered = false;
    private String microchipId;
    private String allergies;
    private String notes;
    private String photoUrl;
    private PetStatus status = PetStatus.ACTIVE;
}
