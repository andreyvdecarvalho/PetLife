package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.auth.entity.User;
import com.petlife.modules.pet.application.port.GroomingRepositoryPort;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Grooming;
import com.petlife.modules.pet.entity.GroomingType;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.infrastructure.dto.GroomingResponse;
import com.petlife.modules.pet.infrastructure.dto.UpdateGroomingRequest;
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
class UpdateGroomingUseCaseTest {

    @Mock
    private GroomingRepositoryPort groomingRepositoryPort;

    @Mock
    private PetRepositoryPort petRepositoryPort;

    @InjectMocks
    private UpdateGroomingUseCase useCase;

    private UUID userId;
    private UUID petId;
    private Pet pet;
    private UUID groomingId;
    private Grooming grooming;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        petId = UUID.randomUUID();
        groomingId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        pet = new Pet();
        pet.setId(petId);
        pet.setUser(user);

        grooming = new Grooming();
        grooming.setId(groomingId);
        grooming.setPet(pet);
        grooming.setType(GroomingType.BATH);
        grooming.setDate(LocalDate.now());
    }

    @Test
    void shouldUpdateGroomingSuccessfully() {
        UpdateGroomingRequest request = new UpdateGroomingRequest();
        request.setType(GroomingType.BATH_AND_GROOMING);
        request.setDate(LocalDate.now());
        request.setProvider("Premium Pet");
        request.setCost(BigDecimal.valueOf(120.00));
        request.setFrequencyDays(30);
        request.setNotes("Fully clean");

        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        when(groomingRepositoryPort.findById(groomingId)).thenReturn(Optional.of(grooming));
        when(groomingRepositoryPort.save(any(Grooming.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GroomingResponse response = useCase.execute(petId, groomingId, userId, request);

        assertNotNull(response);
        assertEquals(GroomingType.BATH_AND_GROOMING, response.getType());
        assertEquals(LocalDate.now().plusDays(30), response.getNextDate());
        assertEquals("Premium Pet", response.getProvider());
    }

    @Test
    void shouldThrowExceptionWhenGroomingDoesNotBelongToPet() {
        UpdateGroomingRequest request = new UpdateGroomingRequest();
        Pet otherPet = new Pet();
        otherPet.setId(UUID.randomUUID());
        grooming.setPet(otherPet);

        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        when(groomingRepositoryPort.findById(groomingId)).thenReturn(Optional.of(grooming));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(petId, groomingId, userId, request));

        assertEquals("GROOMING_PET_MISMATCH", exception.getCode());
    }

    @Test
    void shouldThrowExceptionWhenGroomingNotFound() {
        UpdateGroomingRequest request = new UpdateGroomingRequest();
        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        when(groomingRepositoryPort.findById(groomingId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(petId, groomingId, userId, request));

        assertEquals("GROOMING_NOT_FOUND", exception.getCode());
    }
}
