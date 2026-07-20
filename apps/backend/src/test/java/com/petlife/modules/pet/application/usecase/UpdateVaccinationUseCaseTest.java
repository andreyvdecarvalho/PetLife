package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.pet.application.port.VaccinationPort;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.entity.Vaccination;
import com.petlife.modules.pet.infrastructure.dto.UpdateVaccinationRequest;
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
class UpdateVaccinationUseCaseTest {

    @Mock
    private VaccinationPort vaccinationPort;

    @InjectMocks
    private UpdateVaccinationUseCase useCase;

    private UUID userId;
    private UUID petId;
    private UUID vaccineId;
    private Pet pet;
    private User user;
    private Vaccination vaccination;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        petId = UUID.randomUUID();
        vaccineId = UUID.randomUUID();
        
        user = new User();
        user.setId(userId);
        
        pet = new Pet();
        pet.setId(petId);
        pet.setUser(com.petlife.modules.auth.infrastructure.persistence.mapper.UserMapper.toJpaEntity(user));
        
        vaccination = new Vaccination();
        vaccination.setId(vaccineId);
        vaccination.setPet(pet);
        vaccination.setVaccineName("Old Name");
        vaccination.setDateAdministered(LocalDate.now());
    }

    @Test
    void shouldUpdateVaccinationSuccessfully() {
        UpdateVaccinationRequest request = new UpdateVaccinationRequest();
        request.setVaccineName("New Name");
        request.setDateAdministered(LocalDate.now());

        when(vaccinationPort.findById(vaccineId)).thenReturn(Optional.of(vaccination));
        when(vaccinationPort.save(any(Vaccination.class))).thenReturn(vaccination);

        VaccinationResponse response = useCase.execute(petId, vaccineId, userId, request);

        assertNotNull(response);
        assertEquals("New Name", response.getVaccineName());
        verify(vaccinationPort).save(any(Vaccination.class));
    }

    @Test
    void shouldThrowExceptionWhenVaccinationBelongsToAnotherPet() {
        UpdateVaccinationRequest request = new UpdateVaccinationRequest();
        UUID otherPetId = UUID.randomUUID();

        when(vaccinationPort.findById(vaccineId)).thenReturn(Optional.of(vaccination));

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> useCase.execute(otherPetId, vaccineId, userId, request));
            
        assertEquals("VACCINE_NOT_BELONG", exception.getCode());
    }
}
