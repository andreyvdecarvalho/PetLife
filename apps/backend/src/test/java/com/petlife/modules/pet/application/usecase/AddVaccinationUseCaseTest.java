package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.auth.entity.User;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.application.port.VaccinationPort;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.entity.Vaccination;
import com.petlife.modules.pet.infrastructure.dto.CreateVaccinationRequest;
import com.petlife.modules.pet.infrastructure.dto.VaccinationResponse;
import com.petlife.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddVaccinationUseCaseTest {

    @Mock
    private VaccinationPort vaccinationPort;

    @Mock
    private PetRepositoryPort petPort;

    @InjectMocks
    private AddVaccinationUseCase useCase;

    private UUID userId;
    private UUID petId;
    private Pet pet;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        petId = UUID.randomUUID();
        
        user = new User();
        user.setId(userId);
        
        pet = new Pet();
        pet.setId(petId);
        pet.setUser(user);
    }

    @Test
    void shouldAddVaccinationSuccessfully() {
        CreateVaccinationRequest request = new CreateVaccinationRequest();
        request.setVaccineName("V10");
        request.setDateAdministered(LocalDate.now());
        
        Vaccination savedVaccination = new Vaccination();
        savedVaccination.setId(UUID.randomUUID());
        savedVaccination.setPet(pet);
        savedVaccination.setVaccineName("V10");
        savedVaccination.setDateAdministered(LocalDate.now());

        when(petPort.findById(petId)).thenReturn(Optional.of(pet));
        when(vaccinationPort.save(any(Vaccination.class))).thenReturn(savedVaccination);

        VaccinationResponse response = useCase.execute(petId, userId, request);

        assertNotNull(response);
        assertEquals(savedVaccination.getId(), response.getId());
        assertEquals("V10", response.getVaccineName());
        
        verify(vaccinationPort).save(any(Vaccination.class));
    }

    @Test
    void shouldThrowExceptionWhenPetNotFound() {
        CreateVaccinationRequest request = new CreateVaccinationRequest();
        when(petPort.findById(petId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> useCase.execute(petId, userId, request));
            
        assertEquals("PET_NOT_FOUND", exception.getCode());
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotOwnPet() {
        CreateVaccinationRequest request = new CreateVaccinationRequest();
        UUID otherUserId = UUID.randomUUID();
        
        when(petPort.findById(petId)).thenReturn(Optional.of(pet));

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> useCase.execute(petId, otherUserId, request));
            
        assertEquals("PET_FORBIDDEN", exception.getCode());
    }

    @Test
    void shouldThrowExceptionWhenNextDoseIsBeforeAdministeredDate() {
        CreateVaccinationRequest request = new CreateVaccinationRequest();
        request.setVaccineName("V10");
        request.setDateAdministered(LocalDate.now());
        request.setNextDoseDate(LocalDate.now().minusDays(1));

        when(petPort.findById(petId)).thenReturn(Optional.of(pet));

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> useCase.execute(petId, userId, request));
            
        assertEquals("INVALID_DATE", exception.getCode());
    }
}
