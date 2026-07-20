package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.pet.application.port.GroomingRepositoryPort;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Grooming;
import com.petlife.modules.pet.entity.GroomingType;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.infrastructure.dto.GroomingResponse;
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
class ListGroomingsByPetUseCaseTest {

    @Mock
    private GroomingRepositoryPort groomingRepositoryPort;

    @Mock
    private PetRepositoryPort petRepositoryPort;

    @InjectMocks
    private ListGroomingsByPetUseCase useCase;

    private UUID userId;
    private UUID petId;
    private Pet pet;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        petId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        pet = new Pet();
        pet.setId(petId);
        pet.setUser(com.petlife.modules.auth.infrastructure.persistence.mapper.UserMapper.toJpaEntity(user));
    }

    @Test
    void shouldListGroomingsSuccessfully() {
        Grooming grooming = new Grooming();
        grooming.setId(UUID.randomUUID());
        grooming.setPet(pet);
        grooming.setType(GroomingType.GROOMING);
        grooming.setDate(LocalDate.now());

        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        when(groomingRepositoryPort.findAllByPetId(petId)).thenReturn(List.of(grooming));

        List<GroomingResponse> response = useCase.execute(petId, userId);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(GroomingType.GROOMING, response.get(0).getType());
    }

    @Test
    void shouldThrowExceptionWhenPetNotFound() {
        when(petRepositoryPort.findById(petId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(petId, userId));

        assertEquals("PET_NOT_FOUND", exception.getCode());
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotOwnPet() {
        UUID otherUserId = UUID.randomUUID();
        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(petId, otherUserId));

        assertEquals("FORBIDDEN_PET_ACCESS", exception.getCode());
    }
}
