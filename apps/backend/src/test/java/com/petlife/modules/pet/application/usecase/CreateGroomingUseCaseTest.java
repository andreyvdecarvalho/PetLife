package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.pet.application.port.GroomingRepositoryPort;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Grooming;
import com.petlife.modules.pet.entity.GroomingType;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.infrastructure.dto.CreateGroomingRequest;
import com.petlife.modules.pet.infrastructure.dto.GroomingResponse;
import com.petlife.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateGroomingUseCaseTest {

    @Mock
    private GroomingRepositoryPort groomingRepositoryPort;

    @Mock
    private PetRepositoryPort petRepositoryPort;

    @InjectMocks
    private CreateGroomingUseCase useCase;

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
    void shouldCreateGroomingSuccessfully() {
        CreateGroomingRequest request = new CreateGroomingRequest();
        request.setType(GroomingType.BATH);
        request.setDate(LocalDate.now());
        request.setProvider("Pet Shop");
        request.setCost(BigDecimal.valueOf(50.00));
        request.setFrequencyDays(15);
        request.setNotes("Clean pet");

        Grooming savedGrooming = new Grooming();
        savedGrooming.setId(UUID.randomUUID());
        savedGrooming.setPet(pet);
        savedGrooming.setType(GroomingType.BATH);
        savedGrooming.setDate(request.getDate());
        savedGrooming.setProvider(request.getProvider());
        savedGrooming.setCost(request.getCost());
        savedGrooming.setFrequencyDays(request.getFrequencyDays());
        savedGrooming.setNotes(request.getNotes());
        savedGrooming.calculateNextDate();

        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        when(groomingRepositoryPort.save(any(Grooming.class))).thenReturn(savedGrooming);

        GroomingResponse response = useCase.execute(petId, userId, request);

        assertNotNull(response);
        assertEquals(savedGrooming.getId(), response.getId());
        assertEquals(GroomingType.BATH, response.getType());
        assertEquals(LocalDate.now().plusDays(15), response.getNextDate());
        verify(groomingRepositoryPort).save(any(Grooming.class));
    }

    @Test
    void shouldThrowExceptionWhenPetNotFound() {
        CreateGroomingRequest request = new CreateGroomingRequest();
        when(petRepositoryPort.findById(petId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(petId, userId, request));

        assertEquals("PET_NOT_FOUND", exception.getCode());
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotOwnPet() {
        CreateGroomingRequest request = new CreateGroomingRequest();
        UUID otherUserId = UUID.randomUUID();

        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(petId, otherUserId, request));

        assertEquals("FORBIDDEN_PET_ACCESS", exception.getCode());
    }
}
