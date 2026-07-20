package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.application.port.VaccinationPort;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.entity.Vaccination;
import com.petlife.modules.pet.infrastructure.dto.VaccinationResponse;
import com.petlife.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListVaccinationsByPetUseCaseTest {

    @Mock
    private VaccinationPort vaccinationPort;

    @Mock
    private PetRepositoryPort petPort;

    @InjectMocks
    private ListVaccinationsByPetUseCase useCase;

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
        pet.setUser(com.petlife.modules.auth.infrastructure.persistence.mapper.UserMapper.toJpaEntity(user));
    }

    @Test
    void shouldListVaccinationsSuccessfully() {
        Vaccination v1 = new Vaccination();
        v1.setId(UUID.randomUUID());
        v1.setPet(pet);
        v1.setVaccineName("V10");
        v1.setDateAdministered(LocalDate.now());
        
        when(petPort.findById(petId)).thenReturn(Optional.of(pet));
        when(vaccinationPort.findByPetId(petId)).thenReturn(List.of(v1));

        List<VaccinationResponse> responses = useCase.execute(petId, userId);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(v1.getId(), responses.get(0).getId());
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotOwnPet() {
        UUID otherUserId = UUID.randomUUID();
        when(petPort.findById(petId)).thenReturn(Optional.of(pet));

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> useCase.execute(petId, otherUserId));
            
        assertEquals("PET_FORBIDDEN", exception.getCode());
    }
}
