package com.petlife.shared.factories;

import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.modules.pet.domain.entity.PetSex;
import com.petlife.modules.pet.domain.entity.PetSpecies;
import com.petlife.modules.pet.domain.entity.PetStatus;
import java.time.LocalDate;
import java.util.function.Consumer;

public class PetFactory {

    public static Pet make() {
        return make(p -> {
        });
    }

    public static Pet make(Consumer<Pet> overrides) {
        var pet = new Pet();
        pet.setName("Rex");
        pet.setSpecies(PetSpecies.DOG);
        pet.setSex(PetSex.MALE);
        pet.setStatus(PetStatus.ACTIVE);
        pet.setNeutered(false);
        pet.setBirthDate(LocalDate.of(2020, 1, 1));
        overrides.accept(pet);
        return pet;
    }
}
