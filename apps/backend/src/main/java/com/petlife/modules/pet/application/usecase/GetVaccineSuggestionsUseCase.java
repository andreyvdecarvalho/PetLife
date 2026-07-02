package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.entity.PetSpecies;
import com.petlife.shared.exception.BusinessException;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GetVaccineSuggestionsUseCase {

    private static final List<String> DOG_VACCINES = Arrays.asList(
            "V8/V10",
            "Antirrábica",
            "Gripe",
            "Giardíase",
            "Leishmaniose"
    );

    private static final List<String> CAT_VACCINES = Arrays.asList(
            "V3/V4/V5",
            "Antirrábica",
            "FeLV"
    );

    public List<String> execute(String species) {
        try {
            PetSpecies petSpecies = PetSpecies.valueOf(species.toUpperCase());
            if (petSpecies == PetSpecies.DOG) {
                return DOG_VACCINES;
            } else if (petSpecies == PetSpecies.CAT) {
                return CAT_VACCINES;
            }
            return Arrays.asList();
        } catch (IllegalArgumentException e) {
            throw BusinessException.badRequest("INVALID_SPECIES", "Invalid species type");
        }
    }
}
