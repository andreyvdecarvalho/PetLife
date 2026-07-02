package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.entity.PetSpecies;
import com.petlife.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GetVaccineSuggestionsUseCaseTest {

    @InjectMocks
    private GetVaccineSuggestionsUseCase useCase;

    @Test
    void shouldReturnDogVaccines() {
        List<String> suggestions = useCase.execute("DOG");
        assertNotNull(suggestions);
        assertTrue(suggestions.contains("V8/V10"));
        assertTrue(suggestions.contains("Antirrábica"));
    }

    @Test
    void shouldReturnCatVaccines() {
        List<String> suggestions = useCase.execute("CAT");
        assertNotNull(suggestions);
        assertTrue(suggestions.contains("V3/V4/V5"));
        assertTrue(suggestions.contains("FeLV"));
    }

    @Test
    void shouldThrowExceptionForInvalidSpecies() {
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> useCase.execute("INVALID"));
            
        assertEquals("INVALID_SPECIES", exception.getCode());
    }
}
