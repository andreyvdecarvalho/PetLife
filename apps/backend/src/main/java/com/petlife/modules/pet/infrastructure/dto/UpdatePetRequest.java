package com.petlife.modules.pet.infrastructure.dto;

import com.petlife.modules.pet.domain.entity.PetSex;
import com.petlife.modules.pet.domain.entity.PetSize;
import com.petlife.modules.pet.domain.entity.PetSpecies;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdatePetRequest(
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    String name,

    @NotNull(message = "A espécie é obrigatória")
    PetSpecies species,

    @Size(max = 100, message = "A raça deve ter no máximo 100 caracteres")
    String breed,

    @NotNull(message = "O sexo é obrigatório")
    PetSex sex,

    @PastOrPresent(message = "A data de nascimento não pode ser no futuro")
    LocalDate birthDate,

    BigDecimal weightKg,
    PetSize size,
    boolean neutered,
    String microchipId,
    String allergies,
    String notes
) {}
